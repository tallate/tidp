package com.tallate.tidp.spring;

import com.tallate.tidp.MethodSignatureWrapper;
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
        MethodSignatureWrapper wrapper = new MethodSignatureWrapper(pjp);
        String methodFullName = wrapper.getMethodSignature();
        log.info(">> idempotent intercept method {}", methodFullName);
        Object res = idpInterceptor.doIntercept(wrapper);
        log.info("<< idempotent intercept method {}", methodFullName);
        return res;
    }

}
