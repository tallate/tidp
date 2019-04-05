package com.tallate.test.http;

import com.tallate.test.keyprovider.SpanIdKeyProvider;
import javax.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 */
@Slf4j
@Data
public class HttpInterceptor {

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