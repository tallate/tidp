package com.tallate.test.commonconfig;

import com.tallate.test.http.HttpInterceptor;
import com.tallate.tidp.spring.IdpInterceptor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * @author hgc
 * @date 4/5/19
 */
@Configuration
@Aspect
public class AspectConfig {

  private HttpInterceptor httpInter;

  private IdpInterceptor idpInter;

  @Autowired
  public AspectConfig(HttpInterceptor httpInter, IdpInterceptor idpInter) {
    this.httpInter = httpInter;
    this.idpInter = idpInter;
  }

  /**
   * 幂等切点
   */
  @Pointcut("@annotation(com.tallate.tidp.EnableIdp)")
  public void interceptIdpInter() {
  }

  @Around("interceptIdpInter()")
  @Order(1)
  public Object interceptHttp(ProceedingJoinPoint pjp) throws Throwable {
    return httpInter.intercept(pjp);
  }

  /**
   * 织入通知，注意织入顺序
   */
  @Around("interceptIdpInter()")
  @Order(2)
  public Object interceptIdp(ProceedingJoinPoint pjp) throws Throwable {
    return idpInter.intercept(pjp);
  }


}
