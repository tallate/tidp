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
 * @author tallate
 * @date 1/20/19
 */
public class IdpTest {

  private RestTemplate restTemplate = new RestTemplate();

  @Test
  public void testHello() {
    String res = restTemplate.getForObject("http://localhost:8080/test/idp/hello", String.class);
    System.out.println(res);
  }

  @Test
  public void testSpanId() throws InterruptedException {
    String spanId = "139";
    String url = "http://localhost:8080/test/idp/spanId";
    Map<String, String> headerParams = new HashMap<>();
    headerParams.put("spanId", spanId);
    ExecutorService tp = Executors.newFixedThreadPool(3);
    CountDownLatch latch = new CountDownLatch(3);
    for(int i = 0; i < 3; i++) {
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

}
