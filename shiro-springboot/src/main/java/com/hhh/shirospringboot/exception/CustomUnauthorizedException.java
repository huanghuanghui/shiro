package com.hhh.shirospringboot.exception;

/**
 * @author hhh
 * @date 2020/1/10 9:10
 * @Despriction 自定义401无权限异常(UnauthorizedException)
 */
public class CustomUnauthorizedException extends RuntimeException {

  public CustomUnauthorizedException(String msg) {
    super(msg);
  }

  public CustomUnauthorizedException() {
    super();
  }
}
