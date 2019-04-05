package com.tallate.tidp;

import lombok.AllArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

/**
 */
@AllArgsConstructor
public class MethodSignatureWrapper {

  private ProceedingJoinPoint pjp;

  public Object invoke() throws Throwable {
    return pjp.proceed();
  }

  public String getMethodSignature() {
    return pjp.getSignature().getDeclaringTypeName() + "." + pjp.getSignature().getName();
  }

  /**
   * 获取方法返回值类型
   */
  public Class getReturnType() {
    MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
    return methodSignature.getReturnType();
  }

}
