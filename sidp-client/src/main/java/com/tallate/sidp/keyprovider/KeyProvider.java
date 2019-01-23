package com.tallate.sidp.keyprovider;

/**
 * @author tallate
 * @date 1/18/19
 */
public interface KeyProvider {

  String get() throws KeyGenException;

}
