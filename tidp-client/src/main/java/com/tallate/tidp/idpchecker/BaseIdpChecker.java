package com.tallate.tidp.idpchecker;

import com.tallate.tidp.IdpKey;
import com.tallate.tidp.KeyState;
import com.tallate.tidp.keyprovider.KeyGenException;
import com.tallate.tidp.keyprovider.KeyProvider;
import com.tallate.tidp.keystore.KeyStore;
import com.tallate.tidp.keystore.KeyStoreException;
import com.tallate.tidp.util.KeyGenUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseIdpChecker implements IdpChecker {

  protected KeyProvider keyProvider;

  protected KeyStore keyStore;

  @Override
  public void onException(Throwable cause) throws KeyStoreException, KeyGenException {
    IdpKey idpKey = new IdpKey()
        .setId(keyProvider.get());
    if (RuntimeException.class.isAssignableFrom(cause.getClass())) {
      idpKey.setKeyState(KeyState.RUNTIME_FAIL);
      keyStore.replace(idpKey);
    } else if (Exception.class.isAssignableFrom(cause.getClass())) {
      idpKey.setKeyState(KeyState.FAIL);
      keyStore.replace(idpKey);
    } else {
      // 不支持的异常处理
      throw new IllegalStateException(cause);
    }
  }

}
