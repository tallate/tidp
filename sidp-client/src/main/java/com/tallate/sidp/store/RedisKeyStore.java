package com.tallate.sidp.store;

import com.tallate.sidp.IdpKey;
import com.tallate.sidp.KeyState;
import org.javatuples.Pair;

import java.util.Set;

/**
 * @author tallate
 * @date 1/18/19
 */
public class RedisKeyStore implements KeyStore {

  @Override
  public void replace(IdpKey k) throws KeyStoreException {

  }

  @Override
  public void remove(String id) throws KeyStoreException {

  }

  @Override
  public Pair<IdpKey, Integer> putIfAbsent(IdpKey k) throws KeyStoreException {
    return null;
  }

  @Override
  public Pair<IdpKey, Integer> putIfAbsentOrInStates(IdpKey k, Set<KeyState> states) throws KeyStoreException {
    return null;
  }
}
