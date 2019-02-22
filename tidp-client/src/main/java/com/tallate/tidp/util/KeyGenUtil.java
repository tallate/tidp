package com.tallate.tidp.util;

import com.tallate.tidp.IdpKey;
import com.tallate.tidp.KeyState;
import com.tallate.tidp.keyprovider.KeyGenException;

import java.io.IOException;

import static com.tallate.tidp.Msgs.IDPKEY_COMPRESS_EXCEPTION;

/**
 * @author hgc
 * @date 2/17/19
 */
public class KeyGenUtil {

  public static byte[] serialize(Object res) throws IOException {
    String serialized = JsonUtil.write(res);
    return CompressUtil.zip(serialized);
  }

  public static <T> T deserialize(byte[] bytes, Class<T> type) throws IOException {
    String uncompressed = CompressUtil.unZip(bytes);
    return JsonUtil.read(uncompressed, type);
  }

  public static IdpKey newSuccess(String id, Object res) throws KeyGenException {
    try {
      byte[] serialized = serialize(res);
      return new IdpKey()
          .setId(id)
          .setKeyState(KeyState.SUCCESS)
          .setContent(serialized);
    } catch (IOException e) {
      throw new KeyGenException(IDPKEY_COMPRESS_EXCEPTION, e);
    }
  }

  public static IdpKey newExecuting(String id) {
    return new IdpKey()
        .setId(id)
        .setKeyState(KeyState.EXECUTING);
  }

}
