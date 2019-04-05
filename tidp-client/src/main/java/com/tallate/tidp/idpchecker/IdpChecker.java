package com.tallate.tidp.idpchecker;

import com.tallate.tidp.MethodSignatureWrapper;
import com.tallate.tidp.keyprovider.KeyGenException;
import com.tallate.tidp.keystore.KeyStoreException;

/**
 */
public interface IdpChecker {

  Object onCheck(MethodSignatureWrapper wrapper) throws Throwable;

  void onException(Throwable cause) throws KeyStoreException, KeyGenException;

}
