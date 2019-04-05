package com.tallate.tidp.keystore;

import com.tallate.tidp.IdpKey;
import com.tallate.tidp.util.StringJoinerUtil;

/**
 */
public class KeyStoreException extends Exception {

  public KeyStoreException(String msg, IdpKey k) {
    super(StringJoinerUtil.join(msg, k.toString()));
  }

  public KeyStoreException(String msg, Throwable cause) {
    super(msg, cause);
  }

  public KeyStoreException(String msg, String id, Throwable cause) {
    super(StringJoinerUtil.join(msg, " id=", id), cause);
  }

  public KeyStoreException(String msg, IdpKey k, Throwable cause) {
    super(StringJoinerUtil.join(msg, " ", k.toString()), cause);
  }

}
