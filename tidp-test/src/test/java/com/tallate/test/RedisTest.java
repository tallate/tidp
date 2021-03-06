package com.tallate.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.tallate.tidp.IdpKey;
import com.tallate.tidp.KeyState;
import com.tallate.tidp.keystore.RedisClient;
import com.tallate.tidp.util.FileUtil;
import com.tallate.tidp.util.JsonUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 测试调用redisClient接口执行Lua脚本
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestRedisIdpApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class RedisTest {


  @Autowired
  private RedisTemplate<String, String> redisTemplate;

  @Autowired
  private RedisClient redisClient;

  private List<String> wrap(String... args) {

    return args == null ? new ArrayList<>() : Arrays.asList(args);
  }

  @Test
  public void test1() {
    String luaStr = "return {ARGV[1]}";
    String sha = redisClient.loadScript(luaStr);
    Object res = redisClient.executeLua(sha, new ArrayList<>(), wrap("jack"));
    System.out.println(res);
  }

  @Test
  public void test2() {
    String luaStr = "return {KEYS[1], KEYS[2], ARGV[1], ARGV[2]}";
    String sha = redisClient.loadScript(luaStr);
    Object res = redisClient.executeLua(sha, wrap("username", "age"), wrap("jack", "20"));
    System.out.println(res);
  }

  @Test
  public void testSetGet() throws JsonProcessingException {
    IdpKey idpKey = new IdpKey()
        .setId("123")
        .setKeyState(KeyState.EXECUTING);
    List<String> params = Lists.newLinkedList();
    params.add(JsonUtil.write(idpKey));
    String luaStr = "local newKJson = ARGV[1]\n"
        + "local newK = cjson.decode(newKJson)\n"
        + "redis.call('set', newK.id, newKJson)\n"
        + "local res = redis.call('get', newK.id)\n"
        + "return res";
    String sha = redisClient.loadScript(luaStr);
    Object res = redisClient.executeLua(sha, Lists.newArrayList(), params);
    System.out.println(res);
  }

  @Test
  public void testReturnJson() {
    String luaStr = "local newK = {}\n"
        + "newK['id'] = '123'\n"
        + "newK['score'] = 1234332.1\n"
        + "return cjson.encode(newK)";
    String sha = redisClient.loadScript(luaStr);
    Object res = redisClient.executeLua(sha, wrap(), wrap());
    System.out.println(res);
  }

  @Test
  public void testContainStates() throws JsonProcessingException {
    String luaStr = "local states = cjson.decode(ARGV[1])\n"
        + "states['3'] = '123'\n"
        + "return cjson.encode(states)";
    List<String> params = Lists.newLinkedList();
    Set<String> states = Sets.newHashSet();
    states.add("1");
    states.add("2");
    params.add(JsonUtil.write(states));
    String sha = redisClient.loadScript(luaStr);
    Object res = redisClient.executeLua(sha, Lists.newArrayList(), params);
    System.out.println(res);
  }

  @Test
  public void testPutIfAbsent() throws JsonProcessingException {
    IdpKey idpKey = new IdpKey()
        .setId("123")
        .setKeyState(KeyState.EXECUTING);
    List<String> params = Lists.newLinkedList();
    params.add(JsonUtil.write(idpKey));
    String luaStr = FileUtil.readExternalResFile("/lua/putIfAbsent.lua");
    String sha = redisClient.loadScript(luaStr);
    Object res = redisClient.executeLua(sha, Lists.newArrayList(), params);
    System.out.println(res);
  }

  @Test
  public void testPutIfAbsentOrInStates() throws JsonProcessingException {
    IdpKey idpKey = new IdpKey()
        .setId("126")
        .setKeyState(KeyState.EXECUTING);
    Set<KeyState> states = Sets.newHashSet();
    states.add(KeyState.FAIL);
    List<String> params = Lists.newLinkedList();
    params.add(JsonUtil.write(idpKey));
    params.add(JsonUtil.write(states));
    String luaStr = FileUtil.readExternalResFile("/lua/putIfAbsentOrInStates.lua");
    String sha = redisClient.loadScript(luaStr);
    Object res = redisClient.executeLua(sha, Lists.newArrayList(), params);
    System.out.println(res);
  }

  /**
   * 设置一个键的过期时间
   */
  @Test
  public void testPutExpire() {
    String luaStr = "redis.call('set', 'a', 'a', 'EX', 10)";
    String sha = redisClient.loadScript(luaStr);
    Object res = redisClient.executeLua(sha, wrap(), wrap());
    System.out.println(res);
  }

  /**
   * 事务multi/exec必须在一个Connection中执行，RedisTemplate的multi/exec暂时不支持
   */
  @Test
  public void testMulti() {
    RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();
    connection.multi();
    // 在执行exec之前，所有命令都会被加入到一个队列中，然后直接返回null
    Boolean res1 = connection.set("a".getBytes(), "a".getBytes());
    assert res1 == null;
    byte[] res2 = connection.get("a".getBytes());
    assert res2 == null;
    List<Object> exec = connection.exec();
    // 执行exec的结果是每条命令的结果
    assert (boolean) exec.get(0);
    assert "a".equals(new String((byte[]) exec.get(1)));
  }

}
