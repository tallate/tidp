package com.tallate.tidp.keystore;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.tallate.tidp.IdpKey;
import com.tallate.tidp.KeyState;
import com.tallate.tidp.Msgs;
import com.tallate.tidp.util.NamedThreadFactory;
import com.tallate.tidp.util.Pair;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author tallate
 * @date 1/18/19
 */
@Slf4j
@Data
@Accessors(chain = true)
@SuppressWarnings("all")
public class JdbcKeyStore implements KeyStore {

  private DataSource dataSource;

  /**
   * 插入SQL
   */
  private static final String INSERT_SQL = "insert into idpkey (id, key_state) values(?, ?);";
  /**
   * 保存SQL，如果id已存在则更新
   */
  private static final String SAVE_SQL = "insert into idpkey (id, key_state) values(?, ?) on duplicate key update key_state = ?;";
  /**
   * 查询SQL，加行锁
   */
  private static final String QUERY_LOCKSQL = "select id, key_state from idpkey where id = ? for update;";
  /**
   * 条件查询SQL，in子句需要拼接sql
   */
  private static final String QUERY_INSTATES_LOCKSQL_PREFIX = "select id, key_state from idpkey where id = ? or key_state in (";
  private static final String QUERY_INSTATES_LOCKSQL_SUFFIX = ") for update";
  /**
   * 删除SQL
   */
  private static final String DELETE_SQL = "delete from idpkey where id = ?;";
  /**
   * 过期清理SQL
   */
  private static final String CLEANUP_SQL = "delete from idpkey where created_time < date_add(now(), interval -5 minute);";

  /**
   * 过期清理任务线程池
   */
  private static final ScheduledExecutorService CLEANUP_POOL = Executors
      .newScheduledThreadPool(1, new NamedThreadFactory("idp-cleanup"));

  @PostConstruct
  public void init() {
    // 每隔5分钟清理一次过期tidp
    CLEANUP_POOL.scheduleAtFixedRate(() -> {
          try {
            Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(CLEANUP_SQL);
            pstmt.executeUpdate();
          } catch (SQLException cause) {
            log.error(Msgs.IDPKEY_KEYSTORE_CLEANUP_EXCEPTION, cause);
          }
        },
        EXPIRE_TIME, EXPIRE_TIME, TimeUnit.SECONDS);
  }

  @Override
  public void replace(IdpKey k) throws KeyStoreException {
    try {
      Connection conn = dataSource.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(SAVE_SQL);
      pstmt.setString(1, k.getId());
      pstmt.setString(2, k.getKeyState().toString());
      pstmt.setString(3, k.getKeyState().toString());
      pstmt.executeUpdate();
    } catch (SQLException cause) {
      throw new KeyStoreException(Msgs.IDPKEY_KEYSTORE_SAVE_EXCEPTION, k, cause);
    }
  }

  /**
   * 数据库中实现swap操作，使用了行锁
   */
  @Override
  public Pair putIfAbsent(IdpKey k) throws KeyStoreException {
    Preconditions.checkNotNull(k);
    try {
      Connection conn = dataSource.getConnection();
      conn.setAutoCommit(false);
      PreparedStatement queryPStmt = conn.prepareStatement(QUERY_LOCKSQL);
      queryPStmt.setString(1, k.getId());
      ResultSet rs = queryPStmt.executeQuery();
      IdpKey oldK;
      Integer updatedCount = 0;
      if (rs.next()) {
        // 如果存在，则将旧值返回
        oldK = new IdpKey()
            .setId(rs.getString(1))
            .setKeyState(Enum.valueOf(KeyState.class, rs.getString(2)));
      } else {
        // 如果不存在，则插入新值
        PreparedStatement savePStmt = conn.prepareStatement(SAVE_SQL);
        savePStmt.setString(1, k.getId());
        savePStmt.setString(2, k.getKeyState().toString());
        savePStmt.setString(3, k.getKeyState().toString());
        updatedCount = savePStmt.executeUpdate();
        oldK = k;
      }
      conn.commit();
      return new Pair(oldK, updatedCount);
    } catch (SQLException cause) {
      log.error(Msgs.IDPKEY_KEYSTORE_SAVE_EXCEPTION, k, cause);
      throw new KeyStoreException(Msgs.IDPKEY_KEYSTORE_SAVE_EXCEPTION, k, cause);
    }
  }

  Map<Integer, String> SQL_CACHE = new HashMap<>();

  private Integer hashing(Set<KeyState> states) {
    int hash = 0;
    for (KeyState state : states) {
      hash += state.getValue();
    }
    return hash;
  }

  private String genQueryInStatesSql(Set<KeyState> states) {
    String sql;
    int hash = hashing(states);
    if (null != (sql = SQL_CACHE.get(hash))) {
      return sql;
    }
    StringBuilder sqlSB = new StringBuilder(QUERY_INSTATES_LOCKSQL_PREFIX);
    KeyState[] ss = states.toArray(new KeyState[0]);
    for (int i = 0; i < ss.length; i++) {
      sqlSB.append("?");
      if (i != ss.length - 1) {
        sqlSB.append(", ");
      }
    }
    sqlSB.append(QUERY_INSTATES_LOCKSQL_SUFFIX);
    sql = sqlSB.toString();
    SQL_CACHE.put(hash, sql);
    return sql;
  }

  @Override
  public Pair putIfAbsentOrInStates(IdpKey k, Set<KeyState> states)
      throws KeyStoreException {
    Preconditions.checkNotNull(k);
    try {
      Connection conn = dataSource.getConnection();
      conn.setAutoCommit(false);
      String sql = genQueryInStatesSql(states);
      PreparedStatement queryPStmt = conn.prepareStatement(sql);
      queryPStmt.setString(1, k.getId());
      KeyState[] ss = states.toArray(new KeyState[0]);
      for (int i = 0; i < ss.length; i++) {
        queryPStmt.setString(2 + i, ss[i].toString());
      }
      ResultSet rs = queryPStmt.executeQuery();
      IdpKey oldK;
      Integer updatedCount = 0;
      if (rs.next()) {
        // 如果存在，则将旧值返回
        oldK = new IdpKey()
            .setId(rs.getString(1))
            .setKeyState(Enum.valueOf(KeyState.class, rs.getString(2)));
      } else {
        // 如果不存在，则插入新值，并将新值返回
        PreparedStatement savePStmt = conn.prepareStatement(SAVE_SQL);
        savePStmt.setString(1, k.getId());
        savePStmt.setString(2, k.getKeyState().toString());
        savePStmt.setString(3, k.getKeyState().toString());
        updatedCount = savePStmt.executeUpdate();
        oldK = k;
      }
      conn.commit();
      return new Pair(oldK, updatedCount);
    } catch (SQLException cause) {
      throw new KeyStoreException(Msgs.IDPKEY_KEYSTORE_SAVE_EXCEPTION, k, cause);
    }
  }

  @Override
  public void remove(String id) throws KeyStoreException {
    Preconditions.checkArgument(!Strings.isNullOrEmpty(id));
    try {
      Connection conn = dataSource.getConnection();
      PreparedStatement pstmt = conn.prepareStatement(DELETE_SQL);
      pstmt.setString(1, id);
      pstmt.executeUpdate();
    } catch (SQLException cause) {
      log.error(Msgs.IDPKEY_KEYSTORE_DELETE_EXCEPTION, id, cause);
      throw new KeyStoreException(Msgs.IDPKEY_KEYSTORE_DELETE_EXCEPTION, id, cause);
    }
  }
}
