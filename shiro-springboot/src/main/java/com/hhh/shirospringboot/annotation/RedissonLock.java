package com.hhh.shirospringboot.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author hhh
 * @date 2020/1/19 15:56
 * @Despriction
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedissonLock {
  /**
   * 需要锁住的参数
   * @return
   */
  int lockIndex()default -1;
  /**
   * 锁多久后自动释放（单位秒）
   */
  int leaseTime() default 10;
}
