package com.tallate.tidp.spring;

import com.tallate.tidp.Msgs;
import com.tallate.tidp.MethodSignatureWrapper;
import com.tallate.tidp.idpchecker.IdpChecker;
import com.tallate.tidp.idpchecker.RejectException;
import com.tallate.tidp.keyprovider.KeyGenException;
import com.tallate.tidp.IdpException;
import com.tallate.tidp.keystore.KeyStoreException;
import lombok.Data;

@Data
public class IdpInterceptor {

    private IdpChecker idpChecker;

    public Object doIntercept(MethodSignatureWrapper wrapper) throws Throwable {
        try {
            return idpChecker.onCheck(wrapper);
        } catch (KeyStoreException cause) {
            throw new IdpException(Msgs.IDPKEY_STORE_EXCEPTION, cause);
        } catch (KeyGenException cause) {
            throw new IdpException(Msgs.IDPKEY_GEN_EXCEPTION, cause);
        } catch (RejectException cause) {
            throw new IdpException(Msgs.IDP_REJECT_EXCEPTION, cause);
        } catch (InterruptedException cause) {
            throw new IdpException(Msgs.IDP_BLOCKINGCHECK_EXCEPTION, cause);
        } catch (Throwable cause) {
            idpChecker.onException(cause);
            throw cause;
        }
    }

}
