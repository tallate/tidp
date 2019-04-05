package com.tallate.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tallate.test.http.HttpUtil;
import com.tallate.tidp.util.CompressUtil;
import com.tallate.tidp.util.JsonUtil;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

/**
 * 需要先启动对应存储中间件和TestIdpApplication
 */
public class IdpTest {

  private RestTemplate restTemplate = new RestTemplate();

  private void sendSingleRequest(String url, Map<String, String> headerParams) {
    String res = HttpUtil.postJson(url, "", headerParams, String.class);
    System.out.println(res);
  }

  private void sendRetryRequest(String url, Map<String, String> headerParams)
      throws InterruptedException {
    ExecutorService tp = Executors.newFixedThreadPool(3);
    CountDownLatch latch = new CountDownLatch(3);
    for (int i = 0; i < 3; i++) {
      tp.submit(() -> {
        try {
          String res = HttpUtil.postJson(url, "", headerParams, String.class);
          System.out.println(res);
        } finally {
          latch.countDown();
        }
      });
    }
    latch.await();
  }

  private String genSpanId() {
    return Long.toString(System.currentTimeMillis());
  }

  @Test
  public void testHi() {
    String res = restTemplate
        .getForObject("http://localhost:8080/test/idp/hi?msg=abc&msgType=A", String.class);
    System.out.println(res);
  }

  /**
   * 该用例需要配置DefaultKeyProvider
   */
  @Test
  public void testHello() {
    String spanId = genSpanId();
    String url = "http://localhost:8080/test/idp/hello";
    Map<String, String> headerParams = new HashMap<>();
    headerParams.put("spanId", spanId);
    sendSingleRequest(url, headerParams);
  }

  /**
   * 该用例需要配置SpanIdKeyProvider
   */
  @Test
  public void testSpanId() throws InterruptedException {
    String spanId = genSpanId();
    String url = "http://localhost:8080/test/idp/spanId";
    Map<String, String> headerParams = new HashMap<>();
    headerParams.put("spanId", spanId);
    sendRetryRequest(url, headerParams);
  }

  /**
   * 该用例需要配置SpanIdKeyProvider 会重试几次
   */
  @Test
  public void testThrowEx() throws InterruptedException {
    String spanId = genSpanId();
    String url = "http://localhost:8080/test/idp/throwEx";
    Map<String, String> headerParams = new HashMap<>();
    headerParams.put("spanId", spanId);
    sendRetryRequest(url, headerParams);
  }

  @Test
  public void testThrowRuntimeEx() throws InterruptedException {
    String spanId = genSpanId();
    String url = "http://localhost:8080/test/idp/throwRuntimeEx";
    Map<String, String> headerParams = new HashMap<>();
    headerParams.put("spanId", spanId);
    sendRetryRequest(url, headerParams);
  }


}
