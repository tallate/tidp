package com.tallate.tidp.spring;

/**
 *
 */
public class DebugConfig {

  public enum Mode {

    /**
     * 正式的
     */
    PROD,
    /**
     * 测试模式，打印日志
     */
    DEBUG

  }

  private static Mode mode = Mode.PROD;

  public static void toggleDebugMode() {
    mode = Mode.DEBUG;
  }

  public static Mode getMode() {
    return mode;
  }

}
