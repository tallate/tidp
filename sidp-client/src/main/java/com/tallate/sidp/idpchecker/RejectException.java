package com.tallate.sidp.idpchecker;

import com.tallate.sidp.IdpKey;
import com.tallate.sidp.util.StringJoinerUtil;

/**
 * @author tallate
 * @date 1/19/19
 */
public class RejectException extends Exception {

  public RejectException(String msg) {
    super(msg);
  }

  public RejectException(String msg, IdpKey idpKey) {
    super(StringJoinerUtil.join(msg, " ", idpKey.toString()));
  }

  public RejectException(String msg, Throwable cause) {
    super(msg, cause);
  }

  public RejectException(String msg, IdpKey idpKey, Throwable cause) {
    super(StringJoinerUtil.join(msg, " ", idpKey.toString()), cause);
  }
}
