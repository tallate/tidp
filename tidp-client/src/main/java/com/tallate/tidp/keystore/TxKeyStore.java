package com.tallate.tidp.keystore;

import com.tallate.tidp.IdpKey;
import com.tallate.tidp.KeyState;
import com.tallate.tidp.util.Pair;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import java.util.List;
import java.util.Set;

/**
 */
public class TxKeyStore extends DbBasedKeyStore implements KeyStore {

  private final DataSourceTransactionManager tm;

  private final JdbcTemplate jdbcTemplate;

  public TxKeyStore(DataSourceTransactionManager tm) {
    this.tm = tm;
    this.jdbcTemplate = new JdbcTemplate(tm.getDataSource());
  }

  @Override
  public void replace(IdpKey k) throws KeyStoreException {
    jdbcTemplate.update(SAVE_SQL, k.getId(), k.getKeyState(), k.getKeyState());
  }

  @Override
  public void remove(String id) throws KeyStoreException {
    jdbcTemplate.update(DELETE_SQL, id);
  }

  @Override
  public Pair putIfAbsent(IdpKey k) throws KeyStoreException {
    List<IdpKey> res = jdbcTemplate.query(QUERY_SQL, new Object[]{k.getId()},
        (rs, rowNum) -> new IdpKey()
            .setId(rs.getString(1))
            .setKeyState(Enum.valueOf(KeyState.class, rs.getString(2))));
    IdpKey oldK;
    int updatedCount = 0;
    // 如果存在，则将旧值返回
    if (res != null && !res.isEmpty()) {
      oldK = res.get(0);
    } else {
      updatedCount = jdbcTemplate.update(SAVE_SQL, k.getId(), k.getKeyState().toString(), k.getKeyState().toString());
      oldK = k;
    }
    return new Pair(oldK, updatedCount);
  }

  @Override
  public Pair putIfAbsentOrInStates(IdpKey k, Set<KeyState> states) throws KeyStoreException {
    List<IdpKey> res = jdbcTemplate.query(QUERY_SQL, new Object[]{k.getId()},
        (rs, rowNum) -> new IdpKey()
            .setId(rs.getString(1))
            .setKeyState(Enum.valueOf(KeyState.class, rs.getString(2))));
    // 如果已存在的情况
    if (res == null || res.isEmpty()) {
      jdbcTemplate.update(SAVE_SQL, k);
      return new Pair(k, 1);
    }
    IdpKey oldK;
    int updatedCount;
    // 如果已经存在，且状态在目标集合内
    if (states.contains(res.get(0).getKeyState())) {
      jdbcTemplate.update(SAVE_SQL, k);
      oldK = res.get(0);
      updatedCount = 1;
    } else {
      oldK = res.get(0);
      updatedCount = 0;
    }
    return new Pair(oldK, updatedCount);
  }
}
