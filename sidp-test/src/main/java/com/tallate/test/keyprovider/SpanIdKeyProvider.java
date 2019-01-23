package com.tallate.test.keyprovider;

import com.tallate.sidp.keyprovider.KeyGenException;
import com.tallate.sidp.keyprovider.KeyProvider;

/**
 * @author tallate
 * @date 1/21/19
 */
public class SpanIdKeyProvider implements KeyProvider {

  private static ThreadLocal<String> spanIdPool = new ThreadLocal<>();

  public static void put(String spanId) {
    spanIdPool.set(spanId);
  }

  public static void remove() {
    spanIdPool.remove();
  }

  @Override
  public String get() throws KeyGenException {
    return spanIdPool.get();
  }
}
