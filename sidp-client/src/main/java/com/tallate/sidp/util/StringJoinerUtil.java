package com.tallate.sidp.util;

import java.util.Arrays;

/**
 * @author tallate
 * @date 1/20/19 3:13 PM
 */
public class StringJoinerUtil {

  public static String join(String var1, String var2, String... others) {
    StringBuilder sb = new StringBuilder();
    sb.append(var1).append(var2);
    if(null != others) {
      Arrays.stream(others).forEach(sb::append);
    }
    return sb.toString();
  }

}
