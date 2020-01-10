package com.hhh.shirospringboot.model.common;

/**
 * 常量
 */
public class Constant {

  private Constant() {
  }


  /***********************token相关constant************************/
  /**
   * 随机数种子
   */
  public final static String AES_SEED="NjE2NTEzMDMwMkI1MjVBNzYzMzZGODNDRkVERkQ5NzE=";
  /**
   * AES密码加密私钥(Base64加密)
   */
  public static final String  ENCRYPT_AES_KEY= "";
  /**
   *JWT认证加密私钥(Base64加密)
   */
  public static final String ENCRYPT_JWT_KEY = "RjVCRkFDQjg1QzI4Mjk4NjgzMjZEQTI3REU2MTExMzY=";
  /**
   *AccessToken过期时间-5分钟-5*60(秒为单位)
   */
  public static final Long ACCESS_TOKEN_EXPIRE_TIME= 1800L;
  /**
   *RefreshToken过期时间-30分钟-30*60(秒为单位)
   */
  public static final Long REFRESH_TOKEN_EXPIRE_TIME = 300L;
  /**
   *Shiro缓存过期时间-5分钟-5*60(秒为单位)(一般设置与AccessToken过期时间一致)
   */
  public static final String SHIOR_CACHE_EXPIRE_TIME= "300";
  /************************************************/


  /**
   * redis-OK
   */
  public static final String OK = "OK";

  /**
   * redis过期时间，以秒为单位，一分钟
   */
  public static final int EXRP_MINUTE = 60;

  /**
   * redis过期时间，以秒为单位，一小时
   */
  public static final int EXRP_HOUR = 60 * 60;

  /**
   * redis过期时间，以秒为单位，一天
   */
  public static final int EXRP_DAY = 60 * 60 * 24;

  /**
   * redis-key-前缀-shiro:cache:
   */
  public static final String PREFIX_SHIRO_CACHE = "shiro:cache:";

  /**
   * redis-key-前缀-shiro:access_token:
   */
  public static final String PREFIX_SHIRO_ACCESS_TOKEN = "shiro:access_token:";

  /**
   * redis-key-前缀-shiro:refresh_token:
   */
  public static final String PREFIX_SHIRO_REFRESH_TOKEN = "shiro:refresh_token:";

  /**
   * JWT-account:
   */
  public static final String ACCOUNT = "account";

  /**
   * JWT-currentTimeMillis:
   */
  public static final String CURRENT_TIME_MILLIS = "currentTimeMillis";

  /**
   * PASSWORD_MAX_LEN
   */
  public static final Integer PASSWORD_MAX_LEN = 8;
}
