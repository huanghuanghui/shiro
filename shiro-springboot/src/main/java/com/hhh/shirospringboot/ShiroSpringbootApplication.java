package com.hhh.shirospringboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@tk.mybatis.spring.annotation.MapperScan("com.hhh.shirospringboot.mapper")
public class ShiroSpringbootApplication {

  public static void main(String[] args) {
    SpringApplication.run(ShiroSpringbootApplication.class, args);
  }
}
