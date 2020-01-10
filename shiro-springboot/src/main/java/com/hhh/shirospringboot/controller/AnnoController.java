package com.hhh.shirospringboot.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hhh
 * @date 2020/1/10 17:59
 * @Despriction 公开接口，跳过token验证
 */
@RestController
@RequestMapping("/anon-api")
public class AnnoController {

  @GetMapping("/test")
  public String test(){
    return "anon";
  }
}
