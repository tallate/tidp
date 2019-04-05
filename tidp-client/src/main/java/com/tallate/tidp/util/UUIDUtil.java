package com.tallate.tidp.util;

import java.util.UUID;

/**
 */
public class UUIDUtil {

  public static String nextUUID() {
    return UUID.randomUUID().toString();
  }

}
