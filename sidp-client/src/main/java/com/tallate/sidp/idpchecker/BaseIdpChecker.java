package com.tallate.sidp.idpchecker;

import com.tallate.sidp.keyprovider.KeyProvider;
import com.tallate.sidp.store.KeyStore;
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

}
