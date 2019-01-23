package com.tallate.sidp.util;

/**
 * @author tallate
 * @date 1/20/19 2:13 PM
 */
public class RuntimeUtil {

  public static int getAvailableProcessors() {
    return Runtime.getRuntime().availableProcessors();
  }

}
