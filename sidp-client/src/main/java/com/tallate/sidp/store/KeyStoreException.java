package com.tallate.sidp.store;

import com.tallate.sidp.IdpKey;
import com.tallate.sidp.util.StringJoinerUtil;

/**
 * @author tallate
 * @date 1/19/19
 */
public class KeyStoreException extends Exception {

  public KeyStoreException(String msg, IdpKey k) {
    super(StringJoinerUtil.join(msg, k.toString()));
  }

  public KeyStoreException(String msg, String id, Throwable cause) {
    super(StringJoinerUtil.join(msg, " id=", id), cause);
  }

  public KeyStoreException(String msg, IdpKey k, Throwable cause) {
    super(StringJoinerUtil.join(msg, " ", k.toString()), cause);
  }

}
