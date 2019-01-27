package com.tallate.sidp;

import lombok.AllArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * @author tallate
 * @date 1/19/19
 */
@AllArgsConstructor
public class SignatureWrapper {

  private ProceedingJoinPoint pjp;

  public Object invoke() throws Throwable {
    return pjp.proceed();
  }

  public String getMethodSignature() {
    return pjp.getSignature().getDeclaringTypeName() + "." + pjp.getSignature().getName();
  }

}
