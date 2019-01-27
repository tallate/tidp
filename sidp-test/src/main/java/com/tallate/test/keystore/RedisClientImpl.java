package com.tallate.test.keystore;

import com.tallate.sidp.keystore.RedisClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.SetParams;

import java.util.List;
import java.util.Map;

@Component
@Data
@Slf4j
public class RedisClientImpl implements RedisClient {

  @Autowired
  private JedisPool jedisPool;

  @Override
  public String set(String key, String value) {
    try (Jedis jedis = jedisPool.getResource()) {
      return jedis.set(key, value);
    }
  }

  @Override
  public String setex(String key, int seconds, String value) {
    try (Jedis jedis = jedisPool.getResource()) {
      return jedis.setex(key, seconds, value);
    }
  }

  @Override
  public String setexnx(String key, int seconds, String value) {

    try (Jedis jedis = jedisPool.getResource()) {
      SetParams setParams = new SetParams()
          .ex(seconds)
          .nx();
      return jedis.set(key, value, setParams);
    }
  }

  @Override
  public long hset(String hkey, String key, String value) {
    try (Jedis jedis = jedisPool.getResource()) {
      return jedis.hset(hkey, key, value);
    }
  }

  @Override
  public long incr(String key) {
    try (Jedis jedis = jedisPool.getResource()) {
      return jedis.incr(key);
    }
  }

  @Override
  public long decr(String key) {
    try (Jedis jedis = jedisPool.getResource()) {
      return jedis.decr(key);
    }
  }

  @Override
  public long del(String key) {
    try (Jedis jedis = jedisPool.getResource()) {
      return jedis.del(key);
    }
  }

  @Override
  public long hdel(String hkey, String key) {
    try (Jedis jedis = jedisPool.getResource()) {
      return jedis.hdel(hkey, key);
    }
  }

  @Override
  public String get(String key) {
    try (Jedis jedis = jedisPool.getResource()) {
      return jedis.get(key);
    }
  }

  @Override
  public String hget(String hkey, String key) {
    try (Jedis jedis = jedisPool.getResource()) {
      return jedis.hget(hkey, key);
    }
  }

  @Override
  public Map<String, String> hgetAll(String hkey) {
    try (Jedis jedis = jedisPool.getResource()) {
      return jedis.hgetAll(hkey);
    }
  }

  @Override
  public long expire(String key, int second) {
    try (Jedis jedis = jedisPool.getResource()) {
      return jedis.expire(key, second);
    }
  }

  @Override
  public long ttl(String key) {
    try (Jedis jedis = jedisPool.getResource()) {
      return jedis.ttl(key);
    }
  }

  @Override
  public String loadScript(String luaScript) {
    try (Jedis jedis = jedisPool.getResource()) {
      return jedis.scriptLoad(luaScript);
    }
  }

  @Override
  public Object executeLua(String sha, List<String> keys, List<String> params) {
    try (Jedis jedis = jedisPool.getResource()) {
      return jedis.evalsha(sha, keys, params);
    }
  }

}
