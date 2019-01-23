package com.tallate.sidp.store;

import com.tallate.sidp.IdpKey;
import com.tallate.sidp.KeyState;
import org.javatuples.Pair;

import java.util.Set;

/**
 * @author tallate
 * @date 1/18/19
 */
public interface KeyStore {

  void replace(IdpKey k) throws KeyStoreException;

  void remove(String id) throws KeyStoreException;

  Pair<IdpKey, Integer> putIfAbsent(IdpKey k) throws KeyStoreException;

  Pair<IdpKey, Integer> putIfAbsentOrInStates(IdpKey k, Set<KeyState> states) throws KeyStoreException;
}
