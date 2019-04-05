package com.tallate.tidp.keyprovider;

/**
 */
public interface KeyProvider {

  String get() throws KeyGenException;

}
