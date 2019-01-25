package com.tallate.sidp;

import com.tallate.sidp.idpchecker.IdpChecker;
import com.tallate.sidp.idpchecker.RejectException;
import com.tallate.sidp.keyprovider.KeyGenException;
import com.tallate.sidp.spring.IdpException;
import com.tallate.sidp.store.KeyStoreException;
import lombok.Data;

@Data
public class IdpInterceptor {

    private IdpChecker idpChecker;

    public Object doIntercept(SignatureWrapper wrapper) throws Throwable {
        Object res;
        try {
            idpChecker.preCheck();
            res = wrapper.invoke();
            idpChecker.postCheck();
        } catch (KeyStoreException cause) {
            throw new IdpException(ExceptionMsgs.IDPKEY_STORE_EXCEPTION, cause);
        } catch (KeyGenException cause) {
            throw new IdpException(ExceptionMsgs.IDPKEY_GEN_EXCEPTION, cause);
        } catch (RejectException cause) {
            throw new IdpException(ExceptionMsgs.IDP_REJECT_EXCEPTION, cause);
        } catch (InterruptedException cause) {
            throw new IdpException(ExceptionMsgs.IDP_BLOCKINGCHECK_EXCEPTION, cause);
        } catch (Throwable cause) {
            idpChecker.onException(cause);
            throw cause;
        }
        return res;
    }

}
