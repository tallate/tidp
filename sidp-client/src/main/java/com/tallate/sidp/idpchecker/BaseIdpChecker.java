package com.tallate.sidp.idpchecker;

import com.tallate.sidp.IdpKey;
import com.tallate.sidp.KeyState;
import com.tallate.sidp.keyprovider.KeyGenException;
import com.tallate.sidp.keyprovider.KeyProvider;
import com.tallate.sidp.keystore.KeyStore;
import com.tallate.sidp.keystore.KeyStoreException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author tallate
 * @date 1/19/19
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
