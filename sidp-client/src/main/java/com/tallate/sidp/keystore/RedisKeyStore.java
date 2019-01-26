package com.tallate.sidp.keystore;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tallate.sidp.ExceptionMsgs;
import com.tallate.sidp.IdpKey;
import com.tallate.sidp.KeyState;
import com.tallate.sidp.util.JsonUtil;
import java.util.Set;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.javatuples.Pair;

/**
 * @author tallate
 * @date 1/18/19
 */
@Data
@Slf4j
public class RedisKeyStore implements KeyStore {

    private RedisClient redisClient;

    private static final String KEY_PREFIX = "idp-";

    private static final String PUTIFABSENT_LUASCRIPT = "";

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

    @Override
    public Pair<IdpKey, Integer> putIfAbsent(IdpKey k) throws KeyStoreException {
        return null;
    }

    @Override
    public Pair<IdpKey, Integer> putIfAbsentOrInStates(IdpKey k, Set<KeyState> states)
            throws KeyStoreException {
        return null;
    }
}
