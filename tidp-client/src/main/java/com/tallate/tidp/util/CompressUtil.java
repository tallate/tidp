package com.tallate.tidp.util;

import com.google.common.base.Strings;
import com.sun.xml.internal.ws.util.UtilException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * 数据压缩
 * 1. gzip和zip，gzip相对来说压缩比率更高
 *
 * @author hgc
 * @date 1/30/19
 */
public class CompressUtil {

  public static byte[] gzip(String str) throws IOException {
    if (Strings.isNullOrEmpty(str)) {
      return new byte[0];
    }
    return compressByGZIP(str, "UTF-8");
  }

  public static byte[] compressByGZIP(String str, String encoding) throws IOException {
    try (ByteArrayOutputStream out = new ByteArrayOutputStream();
         GZIPOutputStream gzip = new GZIPOutputStream(out)) {
      gzip.write(str.getBytes(encoding));
      return out.toByteArray();
    }
  }

  /**
   * 使用zip格式解压 1. 默认使用UTF-8编码
   */
  public static String unGZIP(byte[] bytes) throws IOException {
    if (null == bytes || 0 == bytes.length) {
      return "";
    }
    return extByGZIP(bytes, "UTF-8");
  }

  private static String extByGZIP(byte[] bytes, String encoding) throws UtilException, IOException {
    String ret;
    try (GZIPInputStream gi = new GZIPInputStream(new ByteArrayInputStream(bytes))) {
      ret = FileUtil.readStream(gi, encoding);
    }
    return ret;
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
   * 使用zip格式解压 1. 默认使用UTF-8编码
   */
  public static String unZip(byte[] bytes) throws IOException {
    if (null == bytes || 0 == bytes.length) {
      return "";
    }
    return extByZip(bytes, "UTF-8");
  }

  /**
   * 使用zip格式压缩 1. 需要指定编码
   */
  private static byte[] compressByZip(String str, String encoding) throws IOException {
    try (ByteArrayOutputStream out = new ByteArrayOutputStream();
         ZipOutputStream gzip = new ZipOutputStream(out)) {
      gzip.write(str.getBytes(encoding));
      return out.toByteArray();
    }
  }

  private static String extByZip(byte[] bytes, String encoding) throws UtilException, IOException {
    String ret;
    try (ZipInputStream gi = new ZipInputStream(new ByteArrayInputStream(bytes))) {
      ret = FileUtil.readStream(gi, encoding);
    }
    return ret;
  }

}
