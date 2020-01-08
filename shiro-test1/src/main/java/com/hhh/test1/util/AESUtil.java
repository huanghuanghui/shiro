package com.hhh.test1.util;

import com.alibaba.druid.util.StringUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * @author hhh
 * @date 2020/1/8 15:24
 * @Despriction
 */
public class AESUtil {
  /**
   * base64后的原始字串
   */
  private static final String BASE64_KEY = "Sk5fU1NSX0FFU19rZXlfKg==";


  /**
   * 加密
   *
   * @param input
   * @param key
   * @return
   * @throws InvalidKeyException
   * @throws NoSuchPaddingException
   * @throws NoSuchAlgorithmException
   * @throws UnsupportedEncodingException
   * @throws BadPaddingException
   * @throws IllegalBlockSizeException
   */
  public static String encrypt(String input, byte[] key) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, UnsupportedEncodingException, BadPaddingException, IllegalBlockSizeException {
    SecretKeySpec adsKey = new SecretKeySpec(key, "AES");
    Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
    cipher.init(Cipher.ENCRYPT_MODE, adsKey);
    byte[] cryPwd = cipher.doFinal(input.getBytes("utf-8"));
    String result = new String(Base64.getEncoder().encode(cryPwd));
    return result;
  }

  public static String encrypt(String input) {
    String pwd = "";
    try {
      pwd = encrypt(input, Base64.getDecoder().decode(BASE64_KEY.getBytes()));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return pwd;
  }

  /**
   * 解密
   *
   * @param input
   * @param key
   * @return
   * @throws InvalidKeyException
   * @throws IOException
   * @throws BadPaddingException
   * @throws IllegalBlockSizeException
   * @throws NoSuchPaddingException
   * @throws NoSuchAlgorithmException
   */
  public static String decrypt(String input, byte[] key) throws InvalidKeyException, IOException, BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException {
    byte[] str = Base64.getDecoder().decode(input);
    SecretKeySpec aesKey = new SecretKeySpec(key, "AES");
    Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
    cipher.init(Cipher.DECRYPT_MODE, aesKey);
    byte[] bits = cipher.doFinal(str);
    return new String(bits, "utf-8");
  }

  public static String decrypt(String input) {
    String pwd = "";
    try {
      pwd = decrypt(input, Base64.getDecoder().decode(BASE64_KEY.getBytes()));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return pwd;
  }


  /**
   * 生成key
   *
   * @param key
   * @return
   */
  public static String getKey(String key) {
    if (StringUtils.isEmpty(key) || key.length() != 16) {

    }
    return new String(Base64.getEncoder().encode(key.getBytes()));
  }

  public static void main(String[] args) throws Exception {
    System.out.println(decrypt("wVRsNWYBg2F8brcQTvBjXQ=="));
  }
}
