package com.tallate.tidp.keystore;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Lists;
import com.tallate.tidp.IdpKey;
import com.tallate.tidp.KeyState;
import com.tallate.tidp.Msgs;
import com.tallate.tidp.util.FileUtil;
import com.tallate.tidp.util.JsonUtil;
import com.tallate.tidp.util.Pair;
import com.tallate.tidp.util.StringJoinerUtil;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

/**
 */
@Data
@Slf4j
@Accessors(chain = true)
public class RedisKeyStore implements KeyStore {

    private RedisClient redisClient;

    private static final String KEY_PREFIX = "idp-";

    private static final String PUTIFABSENT_SCRIPT_PATH = "/lua/putIfAbsent.lua";

    private static volatile String PUTIFABSENT_SCRIPT_SHA;

    private static final String PUTIFABSENTORINSTATES_SCRIPT_PATH = "/lua/putIfAbsentOrInStates.lua";

    private static volatile String PUTIFABSENTORINSTATES_SCRIPT_SHA;

    private String loadScript(String fileName) {
        String content = FileUtil.readExternalResFile(fileName);
        return redisClient.loadScript(content);
    }

    private String serJson(Object obj) throws KeyStoreException {
        try {
            return JsonUtil.write(obj);
        } catch (JsonProcessingException e) {
            throw new KeyStoreException(StringJoinerUtil.join(Msgs.IDPKEY_KEYSTORE_SERIALIZING_EXCEPTION, obj.toString()), e);
        }
    }

    private <T> T deserJson(String json, Class<T> type) throws KeyStoreException {
        try {
            return JsonUtil.read(json, type);
        } catch (IOException e) {
            throw new KeyStoreException(StringJoinerUtil.join(Msgs.IDPKEY_KEYSTORE_SERIALIZING_EXCEPTION, json), e);
        }
    }

    /**
     * 使用Redis执行Lua脚本
     */
    private Pair executeScript(String sha, String... params) throws KeyStoreException {
        List<String> paramList = Lists.newArrayList();
        if (params != null) {
            paramList.addAll(Arrays.asList(params));
        }
        Object res = redisClient.executeLua(sha, Lists.newArrayList(), paramList);
        if (!(res instanceof String)) {
            return null;
        }
        return deserJson((String) res, Pair.class);
    }

    @PostConstruct
    public void init() {
        PUTIFABSENT_SCRIPT_SHA = loadScript(PUTIFABSENT_SCRIPT_PATH);
        PUTIFABSENTORINSTATES_SCRIPT_SHA = loadScript(PUTIFABSENTORINSTATES_SCRIPT_PATH);
    }

    @Override
    public void replace(IdpKey k) throws KeyStoreException {
        String json = serJson(k);
        redisClient.setex(KEY_PREFIX + k.getId(), (int) EXPIRE_TIME, json);
    }

    @Override
    public void remove(String id) throws KeyStoreException {
        redisClient.del(id);
    }

    @Override
    public Pair putIfAbsent(IdpKey k) throws KeyStoreException {
        String kJson = serJson(k);
        String expireTime = Long.toString(EXPIRE_TIME);
        return executeScript(PUTIFABSENT_SCRIPT_SHA, kJson, expireTime);
    }

    @Override
    public Pair putIfAbsentOrInStates(IdpKey k, Set<KeyState> states)
            throws KeyStoreException {
        String kJson = serJson(k);
        String statesJson = serJson(states);
        String expireTime = Long.toString(EXPIRE_TIME);
        return executeScript(PUTIFABSENTORINSTATES_SCRIPT_SHA, kJson, statesJson, expireTime);
    }
}
