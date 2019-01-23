package com.tallate.sidp.spring;

import com.tallate.sidp.ExceptionMsgs;
import com.tallate.sidp.SignatureWrapper;
import com.tallate.sidp.idpchecker.IdpChecker;
import com.tallate.sidp.idpchecker.RejectException;
import com.tallate.sidp.keyprovider.KeyGenException;
import com.tallate.sidp.store.KeyStoreException;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;

/**
 * @author tallate
 * @date 1/19/19
 */
@Slf4j
@Accessors(chain = true)
@Data
public class IdpAspect {

  private IdpChecker idpChecker;

  public Object intercept(ProceedingJoinPoint pjp) throws Throwable {
    SignatureWrapper wrapper = new SignatureWrapper(pjp);
    String methodFullName = wrapper.getMethodSignature();
    log.info(">> idempotent intercept method {}", methodFullName);
    Object res = doIntercept(wrapper);
    log.info("<< idempotent intercept method {}", methodFullName);
    return res;
  }

  private Object doIntercept(SignatureWrapper wrapper) throws Throwable {
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
