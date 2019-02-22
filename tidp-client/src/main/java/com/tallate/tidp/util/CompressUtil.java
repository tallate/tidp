package com.tallate.tidp.util;

import com.google.common.base.Strings;
import com.sun.xml.internal.ws.util.UtilException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * 数据压缩
 *
 * @author hgc
 * @date 1/30/19
 */
public class CompressUtil {

  /**
   * 使用zip格式解压 1. 默认使用UTF-8编码
   */
  public static String unZip(byte[] bytes) throws IOException {
    if (null == bytes || 0 == bytes.length) {
      return "";
    }
    return extByZip(bytes, "UTF-8");
  }

  /**
   * 使用zip格式压缩
   */
  public static byte[] zip(String str) throws IOException {
    if (Strings.isNullOrEmpty(str)) {
      return new byte[0];
    }
    return compressByZip(str, "UTF-8");
  }

  /**
   * 使用zip格式压缩 1. 需要指定编码
   */
  private static byte[] compressByZip(String str, String encoding) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    try (GZIPOutputStream gzip = new GZIPOutputStream(out)) {
      gzip.write(str.getBytes(encoding));
    }
    return out.toByteArray();
  }

  private static String extByZip(byte[] bytes, String encoding) throws UtilException, IOException {
    String ret;
    try (GZIPInputStream gi = new GZIPInputStream(new ByteArrayInputStream(bytes))) {
      ret = FileUtil.readStream(gi, encoding);
    }
    return ret;
  }

}
