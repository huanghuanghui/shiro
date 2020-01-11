package com.hhh.shirospringboot.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.hhh.shirospringboot.exception.CustomException;
import com.hhh.shirospringboot.model.common.Constant;
import com.hhh.shirospringboot.util.common.Base64ConvertUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * JAVA-JWT工具类
 */
@Component
@Log4j2
public class JwtUtil {
  /**
   * 校验token是否正确
   */
  public static boolean verify(String token) {
    try {
      // 帐号加JWT私钥解密
      String secret = getClaim(token, Constant.ACCOUNT) + Base64ConvertUtil.decode(Constant.ENCRYPT_JWT_KEY);
      Algorithm algorithm = Algorithm.HMAC256(secret);
      JWTVerifier verifier = JWT.require(algorithm).build();
      verifier.verify(token);
      return true;
    } catch (UnsupportedEncodingException e) {
      log.error("JWTToken认证解密出现UnsupportedEncodingException异常:{}", e.getMessage());
      throw new CustomException("JWTToken认证解密出现UnsupportedEncodingException异常:" + e.getMessage());
    }
  }

  /**
   * 获得Token中的信息无需secret解密也能获得
   */
  public static String getClaim(String token, String claim) {
    try {
      DecodedJWT jwt = JWT.decode(token);
      // 只能输出String类型，如果是其他类型返回null
      return jwt.getClaim(claim).asString();
    } catch (JWTDecodeException e) {
      log.error("解密Token中的公共信息出现JWTDecodeException异常:{}", e.getMessage());
      throw new CustomException("解密Token中的公共信息出现JWTDecodeException异常:" + e.getMessage());
    }
  }

  /**
   * 生成签名,可以使用getClaim()反解码token中的account信息
   */
  public static String sign(String account, String currentTimeMillis) {
    try {
      // 帐号加JWT私钥加密
      String secret = account + Base64ConvertUtil.decode(Constant.ENCRYPT_JWT_KEY);
      // 此处过期时间是以毫秒为单位，所以乘以1000
      Date date = new Date(System.currentTimeMillis() + Constant.ACCESS_TOKEN_EXPIRE_TIME * 1000);
      Algorithm algorithm = Algorithm.HMAC256(secret);
      // 附带account帐号信息
      return JWT.create()
        .withClaim("account", account)
        .withClaim("currentTimeMillis", currentTimeMillis)
        .withExpiresAt(date)
        .sign(algorithm);
    } catch (UnsupportedEncodingException e) {
      log.error("JWTToken加密出现UnsupportedEncodingException异常:{}", e.getMessage());
      throw new CustomException("JWTToken加密出现UnsupportedEncodingException异常:" + e.getMessage());
    }
  }
}
