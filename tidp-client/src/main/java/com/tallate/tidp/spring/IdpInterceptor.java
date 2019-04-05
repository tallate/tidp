package com.tallate.tidp.spring;

import com.tallate.tidp.IdpException;
import com.tallate.tidp.MethodSignatureWrapper;
import com.tallate.tidp.Msgs;
import com.tallate.tidp.idpchecker.IdpChecker;
import com.tallate.tidp.idpchecker.RejectException;
import com.tallate.tidp.keyprovider.KeyGenException;
import com.tallate.tidp.keystore.KeyStoreException;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * 用于定义幂等切面
 *
 */
@Slf4j
@Accessors(chain = true)
@Data
public class IdpInterceptor {

    private IdpChecker idpChecker;

    public Object intercept(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignatureWrapper wrapper = new MethodSignatureWrapper(pjp);
        String methodFullName = wrapper.getMethodSignature();
        log.info(">> idempotent intercept method {}", methodFullName);
        Object res;
        try {
            res = idpChecker.onCheck(wrapper);
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
        log.info("<< idempotent intercept method {}", methodFullName);
        return res;
    }

}
