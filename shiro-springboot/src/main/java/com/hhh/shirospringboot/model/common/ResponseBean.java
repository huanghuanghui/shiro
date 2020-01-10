package com.hhh.shirospringboot.model.common;

import com.hhh.shirospringboot.exception.CustomException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

/**
 * ResponseBean 通用返回DTO
 */
@Getter
@Setter
public class ResponseBean<T> {
  /**
   * HTTP状态码
   */
  private Integer code;

  /**
   * 返回信息
   */
  private String msg;

  /**
   * 返回的数据
   */
  private T data;

  public ResponseBean(int code, String msg, T data) {
    this.code = code;
    this.msg = msg;
    this.data = data;
  }

  public static ResponseBean error(CustomException e) {
    return new ResponseBean(HttpStatus.EXPECTATION_FAILED.value(),e.getMessage(),"情求失败！");
  }

  public static ResponseBean errorCustomUnauthorizedException(CustomException e) {
    return new ResponseBean(HttpStatus.UNAUTHORIZED.value(),e.getMessage(),"您无权访问该资源！");
  }
}
