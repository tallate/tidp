package com.tallate.sidp.keystore;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Lists;
import com.tallate.sidp.ExceptionMsgs;
import com.tallate.sidp.IdpKey;
import com.tallate.sidp.KeyState;
import com.tallate.sidp.util.FileUtil;
import com.tallate.sidp.util.JsonUtil;
import com.tallate.sidp.util.Pair;
import com.tallate.sidp.util.StringJoinerUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * @author tallate
 * @date 1/18/19
 */
@Data
@Slf4j
public class RedisKeyStore implements KeyStore {

  private RedisClient redisClient;

  private static final String KEY_PREFIX = "idp-";

  private static final String PUTIFABSENT_SCRIPT_PATH = "/lua/putIfAbsent.lua";

  private static volatile String PUTIFABSENT_SCRIPT_SHA;

  private static final String PUTIFABSENTORINSTATES_SCRIPT_PATH = "/lua/putIfAbsentOrInStates.lua";

  private static volatile String PUTIFABSENTORINSTATES_SCRIPT_SHA;

  @PostConstruct
  public void init() {
    String putifabsentScriptContent = FileUtil.readExternalResFile(PUTIFABSENT_SCRIPT_PATH);
    PUTIFABSENT_SCRIPT_SHA = redisClient.loadScript(putifabsentScriptContent);
    String putifabsentorinstatesScriptContent = FileUtil.readExternalResFile(PUTIFABSENTORINSTATES_SCRIPT_PATH);
    PUTIFABSENTORINSTATES_SCRIPT_SHA = redisClient.loadScript(putifabsentorinstatesScriptContent);
  }

  private String serialize(IdpKey k) throws KeyStoreException {
    try {
      return JsonUtil.write(k);
    } catch (JsonProcessingException e) {
      // won't reach here
      throw new KeyStoreException(ExceptionMsgs.IDPKEY_KEYSTORE_SERIALIZING_EXCEPTION, k, e);
    }
  }

  @Override
  public void replace(IdpKey k) throws KeyStoreException {
    String json = serialize(k);
    redisClient.setex(KEY_PREFIX + k.getId(), (int) EXPIRE_TIME, json);
  }

  @Override
  public void remove(String id) throws KeyStoreException {
    redisClient.del(id);
  }

  private String serJson(Object obj) throws KeyStoreException {
    try {
      return JsonUtil.write(obj);
    } catch (JsonProcessingException e) {
      throw new KeyStoreException(StringJoinerUtil.join(ExceptionMsgs.IDPKEY_KEYSTORE_SERIALIZING_EXCEPTION, obj.toString()), e);
    }
  }

  private <T> T deserJson(String json, Class<T> type) throws KeyStoreException {
    try {
      return JsonUtil.read(json, type);
    } catch (IOException e) {
      throw new KeyStoreException(StringJoinerUtil.join(ExceptionMsgs.IDPKEY_KEYSTORE_SERIALIZING_EXCEPTION, json), e);
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public Pair putIfAbsent(IdpKey k) throws KeyStoreException {
    List<String> params = Lists.newLinkedList();
    String json = serJson(k);
    params.add(json);
    Object res = redisClient.executeLua(PUTIFABSENT_SCRIPT_SHA, Lists.newArrayList(), params);
    if (!(res instanceof String)) {
      return null;
    }
    return deserJson((String) res, Pair.class);
  }

  @Override
  @SuppressWarnings("unchecked")
  public Pair putIfAbsentOrInStates(IdpKey k, Set<KeyState> states)
      throws KeyStoreException {
    List<String> params = Lists.newLinkedList();
    String kJson = serJson(k);
    String statesJson = serJson(states);
    params.add(kJson);
    params.add(statesJson);
    Object res = redisClient.executeLua(PUTIFABSENTORINSTATES_SCRIPT_SHA, Lists.newArrayList(), params);
    if (!(res instanceof String)) {
      return null;
    }
    return deserJson((String) res, Pair.class);
  }
}
