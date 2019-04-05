package com.tallate.test.controller;

import com.tallate.tidp.EnableIdp;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 */
@RestController
@RequestMapping("/test/idp")
public class TestIdpController {

    public static enum MsgType {
        A, B
    }

    @RequestMapping("/hi")
    public MsgType hi(@RequestParam("msg") String msg, @RequestParam("msgType") MsgType msgType) {
        return msgType;
    }

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

    @EnableIdp
    @RequestMapping("/throwEx")
    public String throwEx() throws Exception {
        System.out.println(">> 请求处理中...");
        throw new Exception("故意抛出的Exception");
    }

    @EnableIdp
    @RequestMapping("/throwRuntimeEx")
    public String throwRuntimeEx() {
        System.out.println(">> 请求处理中...");
        throw new RuntimeException("故意抛出的RuntimeException");
    }

}
