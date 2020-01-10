package com.hhh.shirospringboot.exception;

/**
 * @author hhh
 * @date 2020/1/10 9:10
 * @Despriction 自定义异常(CustomException)
 */
public class CustomException extends RuntimeException {

  public CustomException(String msg) {
    super(msg);
  }

  public CustomException() {
    super();
  }
}
