package com.tallate.tidp.keystore;

import com.tallate.tidp.IdpKey;
import com.tallate.tidp.KeyState;
import com.tallate.tidp.util.Pair;

import java.util.Set;

/**
 * @author tallate
 * @date 1/18/19
 */
public interface KeyStore {

  /**
   * 超时时间，超过则清除 / s
   */
  long EXPIRE_TIME = 300;

  void replace(IdpKey k) throws KeyStoreException;

  void remove(String id) throws KeyStoreException;

  Pair putIfAbsent(IdpKey k) throws KeyStoreException;

  Pair putIfAbsentOrInStates(IdpKey k, Set<KeyState> states) throws KeyStoreException;
}
