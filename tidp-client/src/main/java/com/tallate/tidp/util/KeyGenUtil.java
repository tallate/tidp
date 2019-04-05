package com.tallate.tidp.util;

import static com.tallate.tidp.Msgs.IDPKEY_COMPRESS_EXCEPTION;

import com.tallate.tidp.IdpKey;
import com.tallate.tidp.KeyState;
import com.tallate.tidp.keyprovider.KeyGenException;
import java.io.IOException;
import java.util.Base64;

/**
 *
 */
public class KeyGenUtil {

  public static byte[] serialize(Object res) throws IOException {
    String serialized = JsonUtil.write(res);
    byte[] compressed = CompressUtil.gzip(serialized);
    return Base64.getEncoder().encode(compressed);
  }

  public static <T> T deserialize(byte[] bytes, Class<T> type) throws IOException {
    byte[] decoded = Base64.getDecoder().decode(bytes);
    String uncompressed = CompressUtil.unGZIP(decoded);
    try {
      return JsonUtil.read(uncompressed, type);
    } catch (IOException e) {
      throw new IOException("反序列化失败：" + new String(bytes) + " -> " + type.getName(), e);
    }
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
