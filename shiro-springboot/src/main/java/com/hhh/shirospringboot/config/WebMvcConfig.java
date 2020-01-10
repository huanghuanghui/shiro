package com.hhh.shirospringboot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author hhh
 * @date 2020/1/10 9:10
 * @Despriction 全局跨域放开
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {
  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**").allowedOrigins("*")
      .allowedMethods("GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS")
      .allowCredentials(false).maxAge(3600);
  }
}
