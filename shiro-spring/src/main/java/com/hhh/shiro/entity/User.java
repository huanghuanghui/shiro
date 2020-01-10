package com.hhh.shiro.entity;

import java.io.Serializable;

/**
 * @author hhh
 * @date 2020/1/9 10:07
 * @Despriction
 */
public class User implements Serializable {
  private String username;
  private String password;
  private Boolean rememberMe=false;

  public Boolean getRememberMe() {
    return rememberMe;
  }

  public void setRememberMe(Boolean rememberMe) {
    this.rememberMe = rememberMe;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
