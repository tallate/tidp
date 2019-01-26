package com.tallate.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestIdpApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)

public class RedisLuaTest {

    @Autowired
    private Jedis jedis;

    @Test
    public void testHello() {
        String luaStr = "return {KEYS[1], KEYS[2], ARGV[1], ARGV[2]}";
        String lua = jedis.scriptLoad(luaStr);
        Object res = jedis.evalsha(lua, 2, "username", "age", "jack", "20");
        System.out.println(res);
    }


}
