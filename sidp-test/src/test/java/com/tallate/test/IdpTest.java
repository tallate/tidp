package com.tallate.test;

import com.tallate.test.http.HttpUtil;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 需要先启动对应存储中间件和TestIdpApplication
 *
 * @author tallate
 * @date 1/20/19
 */
public class IdpTest {

  private RestTemplate restTemplate = new RestTemplate();

  /**
   * 该用例需要配置DefaultKeyProvider
   */
  @Test
  public void testHello() {
    String res = restTemplate.getForObject("http://localhost:8080/test/idp/hello", String.class);
    System.out.println(res);
  }

  private void sendRetryRequest(String url, Map<String, String> headerParams) throws InterruptedException {
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

  /**
   * 该用例需要配置SpanIdKeyProvider
   */
  @Test
  public void testSpanId() throws InterruptedException {
    String spanId = "150";
    String url = "http://localhost:8080/test/idp/spanId";
    Map<String, String> headerParams = new HashMap<>();
    headerParams.put("spanId", spanId);
    sendRetryRequest(url, headerParams);
  }

  /**
   * 该用例需要配置SpanIdKeyProvider
   * 会重试几次
   */
  @Test
  public void testThrowEx() throws InterruptedException {
    String spanId = "152";
    String url = "http://localhost:8080/test/idp/throwEx";
    Map<String, String> headerParams = new HashMap<>();
    headerParams.put("spanId", spanId);
    sendRetryRequest(url, headerParams);
  }

  @Test
  public void testThrowRuntimeEx() throws InterruptedException {
    String spanId = "160";
    String url = "http://localhost:8080/test/idp/throwRuntimeEx";
    Map<String, String> headerParams = new HashMap<>();
    headerParams.put("spanId", spanId);
    sendRetryRequest(url, headerParams);
  }


}
