package com.tallate.tidp.util;

/**
 * @author tallate
 * @date 1/20/19
 */
public class RuntimeUtil {

  public static int getAvailableProcessors() {
    return Runtime.getRuntime().availableProcessors();
  }

}
