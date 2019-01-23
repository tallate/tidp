package com.tallate.test;

import com.tallate.sidp.EnableIdp;
import com.tallate.test.keyprovider.SpanIdKeyProvider;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author tallate
 * @date 1/20/19 5:59 PM
 */
@RestController
@RequestMapping("/test/idp")
public class TestIdpController {

  @EnableIdp
  @RequestMapping("/hello")
  public String hello() {
    return "Hello";
  }

  @EnableIdp
  @RequestMapping("/spanId")
  public String spanId() throws InterruptedException {
    System.out.println(">> 请求处理中...");
    Thread.sleep(1000);
    System.out.println("<< 请求处理完毕...");
    return "OK";
  }

}
