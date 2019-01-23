package com.tallate.sidp.keyprovider;

import com.tallate.sidp.util.UUIDUtil;

/**
 * 生成idpKey的id
 * Default在每次接收到请求都生成一个新的id，所以幂等性检查不会生效
 *
 * @author tallate
 * @date 1/19/19
 */
public class DefaultKeyProvider implements KeyProvider {

  @Override
  public String get() {
    return UUIDUtil.nextUUID();
  }

}
