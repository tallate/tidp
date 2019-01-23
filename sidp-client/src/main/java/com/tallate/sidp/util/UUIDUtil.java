package com.tallate.sidp.util;

import java.util.UUID;

/**
 * @author tallate
 * @date 1/19/19 12:08 AM
 */
public class UUIDUtil {

  public static String nextUUID() {
    return UUID.randomUUID().toString();
  }

}
