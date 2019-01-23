package com.tallate.sidp.idpchecker;

import com.tallate.sidp.keyprovider.KeyGenException;
import com.tallate.sidp.store.KeyStoreException;

/**
 * @author tallate
 * @date 1/18/19
 */
public interface IdpChecker {

  void preCheck() throws RejectException, KeyGenException, KeyStoreException, InterruptedException;

  void postCheck() throws KeyStoreException, KeyGenException;

  void onException(Throwable cause) throws KeyStoreException, KeyGenException;

}
