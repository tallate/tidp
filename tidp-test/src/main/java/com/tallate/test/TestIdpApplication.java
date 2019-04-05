package com.tallate.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

/**
 */
@SpringBootApplication(scanBasePackages = {"com.tallate.test"})
//@ImportResource("classpath*:spring/*.xml")
@PropertySource({/*"classpath:properties/mysql.properties",*/ "classpath:properties/redis.properties"})
public class TestIdpApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestIdpApplication.class, args);
    }

}
