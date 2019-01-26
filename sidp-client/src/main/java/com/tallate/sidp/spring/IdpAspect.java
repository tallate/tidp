package com.tallate.sidp.spring;

import com.tallate.sidp.SignatureWrapper;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * @author tallate
 * @date 1/19/19
 */
@Slf4j
@Accessors(chain = true)
@Data
public class IdpAspect {

    private IdpInterceptor idpInterceptor;

    public Object intercept(ProceedingJoinPoint pjp) throws Throwable {
        SignatureWrapper wrapper = new SignatureWrapper(pjp);
        String methodFullName = wrapper.getMethodSignature();
        log.info(">> idempotent intercept method {}", methodFullName);
        Object res = idpInterceptor.doIntercept(wrapper);
        log.info("<< idempotent intercept method {}", methodFullName);
        return res;
    }


}
