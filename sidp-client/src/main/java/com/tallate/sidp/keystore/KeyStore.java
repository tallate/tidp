package com.tallate.sidp.keystore;

import com.tallate.sidp.IdpKey;
import com.tallate.sidp.KeyState;
import org.javatuples.Pair;

import java.util.Set;

/**
 * @author tallate
 * @date 1/18/19
 */
public interface KeyStore {

  /**
   * 超时时间，超过则清除
   */
  long EXPIRE_TIME = 300;

  void replace(IdpKey k) throws KeyStoreException;

  void remove(String id) throws KeyStoreException;

  Pair<IdpKey, Integer> putIfAbsent(IdpKey k) throws KeyStoreException;

  Pair<IdpKey, Integer> putIfAbsentOrInStates(IdpKey k, Set<KeyState> states) throws KeyStoreException;
}
