package com.tallate.tidp.keyprovider;

import com.tallate.tidp.util.UUIDUtil;

/**
 * 生成idpKey的id
 * Default在每次接收到请求都生成一个新的id，所以幂等性检查不会生效
 *
 */
public class DefaultKeyProvider implements KeyProvider {

  @Override
  public String get() {
    return UUIDUtil.nextUUID();
  }

}
