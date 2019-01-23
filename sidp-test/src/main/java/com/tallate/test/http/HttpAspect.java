package com.tallate.test.http;

import com.tallate.test.keyprovider.SpanIdKeyProvider;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author tallate
 * @date 1/21/19
 */
@Slf4j
@Data
public class HttpAspect {

  public void httpService() {
  }

  private String getSpanId(ProceedingJoinPoint pjp) {
    HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    return request.getHeader("spanId");
  }

  public Object intercept(ProceedingJoinPoint pjp) throws Throwable {
    String spanId = getSpanId(pjp);
    SpanIdKeyProvider.put(spanId);
    Object res = pjp.proceed();
    SpanIdKeyProvider.remove();
    return res;
  }
}