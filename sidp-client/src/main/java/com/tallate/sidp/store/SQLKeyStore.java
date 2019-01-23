package com.tallate.sidp.store;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.tallate.sidp.ExceptionMsgs;
import com.tallate.sidp.IdpKey;
import com.tallate.sidp.KeyState;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.javatuples.Pair;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author tallate
 * @date 1/18/19
 */
@Slf4j
@Data
@Accessors(chain = true)
@SuppressWarnings("all")
public class SQLKeyStore implements KeyStore {

  private DataSource dataSource;

  private static final String INSERT_SQL = "insert into idpkey (id, key_state) values(?, ?);";
  private static final String SAVE_SQL = "insert into idpkey (id, key_state) values(?, ?) on duplicate key update key_state = ?;";
  private static final String QUERY_LOCKSQL = "select id, key_state from idpkey where id = ? for update;";
  // in子句需要拼接sql
  private static final String QUERY_INSTATES_LOCKSQL_PREFIX = "select id, key_state from idpkey where id = ? or key_state in (";
  private static final String QUERY_INSTATES_LOCKSQL_SUFFIX = ") for update";
  private static final String DELETE_SQL = "delete from idpkey where id = ?;";

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
      throw new KeyStoreException(ExceptionMsgs.IDPKEY_SQLSTORE_SAVE_EXCEPTION, k, cause);
    }
  }

  /**
   * 数据库中实现swap操作，使用了行锁
   */
  @Override
  public Pair<IdpKey, Integer> putIfAbsent(IdpKey k) throws KeyStoreException {
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
      return new Pair<>(oldK, updatedCount);
    } catch (SQLException cause) {
      log.error(ExceptionMsgs.IDPKEY_SQLSTORE_SAVE_EXCEPTION, k, cause);
      throw new KeyStoreException(ExceptionMsgs.IDPKEY_SQLSTORE_SAVE_EXCEPTION, k, cause);
    }
  }

  Map<Set<KeyState>, String> SQL_CACHE = new HashMap<>();

  private String genQueryInStatesSql(Set<KeyState> states) {
    String sql;
    if (null != (sql = SQL_CACHE.get(states))) {
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
    SQL_CACHE.put(states, sql);
    return sql;
  }

  @Override
  public Pair<IdpKey, Integer> putIfAbsentOrInStates(IdpKey k, Set<KeyState> states) throws KeyStoreException {
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
      return new Pair<>(oldK, updatedCount);
    } catch (SQLException cause) {
      throw new KeyStoreException(ExceptionMsgs.IDPKEY_SQLSTORE_SAVE_EXCEPTION, k, cause);
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
      log.error(ExceptionMsgs.IDPKEY_SQLSTORE_DELETE_EXCEPTION, id, cause);
      throw new KeyStoreException(ExceptionMsgs.IDPKEY_SQLSTORE_DELETE_EXCEPTION, id, cause);
    }
  }
}
