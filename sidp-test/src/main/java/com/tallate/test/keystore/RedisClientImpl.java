package com.tallate.test.keystore;

import com.tallate.sidp.keystore.RedisClient;
import java.util.Map;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

@Component
@Data
public class RedisClientImpl implements RedisClient {

    @Autowired
    private Jedis jedis;

    @Override
    public String set(String key, String value) {
        return jedis.set(key, value);
    }

    @Override
    public String setex(String key, int seconds, String value) {
        return jedis.setex(key, seconds, value);
    }

    @Override
    public String setexnx(String key, int seconds, String value) {
        SetParams setParams = new SetParams()
                .ex(seconds)
                .nx();
        return jedis.set(key, value, setParams);
    }

    @Override
    public long hset(String hkey, String key, String value) {
        return jedis.hset(hkey, key, value);
    }

    @Override
    public long incr(String key) {
        return jedis.incr(key);
    }

    @Override
    public long decr(String key) {
        return jedis.decr(key);
    }

    @Override
    public long del(String key) {
        return jedis.del(key);
    }

    @Override
    public long hdel(String hkey, String key) {
        return jedis.hdel(hkey, key);
    }

    @Override
    public String get(String key) {
        return jedis.get(key);
    }

    @Override
    public String hget(String hkey, String key) {
        return jedis.hget(hkey, key);
    }

    @Override
    public Map<String, String> hgetAll(String hkey) {
        return jedis.hgetAll(hkey);
    }

    @Override
    public long expire(String key, int second) {
        return jedis.expire(key, second);
    }

    @Override
    public long ttl(String key) {
        return jedis.ttl(key);
    }

    @Override
    public Object executeLua(String lua) {
        return jedis.evalsha(jedis.scriptLoad(lua));
    }
}
