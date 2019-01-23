package com.tallate.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;

/**
 * @author tallate
 * @date 1/20/19
 */
@SpringBootApplication(scanBasePackages = {"com.tallate.test"})
@ImportResource("classpath*:spring/*.xml")
@PropertySource("classpath:properties/idpDatabase.properties")
public class TestIdpApplication {

  public static void main(String[] args) {
    SpringApplication.run(TestIdpApplication.class, args);
  }

}
