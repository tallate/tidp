package com.tallate.tidp.idpchecker;

import com.tallate.tidp.IdpKey;
import com.tallate.tidp.util.StringJoinerUtil;

/**
 */
public class RejectException extends Exception {

  public RejectException(String msg) {
    super(msg);
  }

  public RejectException(String msg, IdpKey idpKey) {
    super(StringJoinerUtil.join(msg, " ", idpKey == null ? "" : idpKey.toString()));
  }

  public RejectException(String msg, Throwable cause) {
    super(msg, cause);
  }

  public RejectException(String msg, IdpKey idpKey, Throwable cause) {
    super(StringJoinerUtil.join(msg, " ", idpKey.toString()), cause);
  }
}
