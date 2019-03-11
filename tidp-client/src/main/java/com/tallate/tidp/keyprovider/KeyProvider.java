package com.tallate.tidp.keyprovider;

/**
 * @author tallate
 * @date 1/18/19
 */
public interface KeyProvider {

  String get() throws KeyGenException;

}
