package com.tallate.test;

import com.tallate.tidp.spring.DebugConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

/**
 *
 */
@SpringBootApplication(scanBasePackages = {"com.tallate.test.controller",
    "com.tallate.test.commonconfig",
    "com.tallate.test.redisconfig"})
@PropertySource({"classpath:properties/redis.properties"})
public class TestRedisIdpApplication {

  public static void main(String[] args) {
    DebugConfig.toggleDebugMode();
    SpringApplication.run(TestRedisIdpApplication.class, args);
  }

}
