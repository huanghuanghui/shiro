package com.hhh.shirospringboot.util;

import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

@SpringBootApplication
public class SpringConfigTool implements ApplicationContextAware {

  private static ApplicationContext context;// 声明一个静态变量保存

  //获取applicationContext
  public static ApplicationContext getContext() {
    return context;
  }

  //通过name获取 Bean
  public static Object getBean(String beanName) {
    return context.getBean(beanName);
  }

  //通过class获取Bean
  public static <T> T getBean(Class<T> requireType) {
    return context.getBean(requireType);
  }

  @Override
  public void setApplicationContext(ApplicationContext context) throws BeansException {
    SpringConfigTool.context = context;
  }
}
