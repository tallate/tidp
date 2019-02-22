package com.tallate.tidp.keystore;

import com.tallate.tidp.IdpKey;
import com.tallate.tidp.KeyState;
import com.tallate.tidp.util.Pair;

import java.util.Set;

/**
 * 加入事务
 *
 * @author hgc
 * @date 1/30/19
 */
public class TxKeyStore implements KeyStore {
  @Override
  public void replace(IdpKey k) throws KeyStoreException {

  }

  @Override
  public void remove(String id) throws KeyStoreException {

  }

  @Override
  public Pair putIfAbsent(IdpKey k) throws KeyStoreException {
    return null;
  }

  @Override
  public Pair putIfAbsentOrInStates(IdpKey k, Set<KeyState> states) throws KeyStoreException {
    return null;
  }
}
