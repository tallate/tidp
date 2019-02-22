package com.tallate.tidp;

/**
 * @author tallate
 * @date 1/20/19
 */
public class IdpException extends RuntimeException {

  public IdpException(String msg) {
    super(msg);
  }

  public IdpException(String msg, Throwable cause) {
    super(msg, cause);
  }

}
