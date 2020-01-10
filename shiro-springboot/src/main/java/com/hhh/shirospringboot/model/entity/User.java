package com.hhh.shirospringboot.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hhh.shirospringboot.model.valid.group.UserEditValidGroup;
import com.hhh.shirospringboot.model.valid.group.UserLoginValidGroup;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

@Table(name = "user")
@Getter
@Setter
public class User implements Serializable {

  private static final long serialVersionUID = 3342723124953988435L;

  /**
   * ID
   */
  @Id
  @GeneratedValue(generator = "JDBC")
  private Integer id;

  /**
   * 帐号
   */
  @NotNull(message = "帐号不能为空", groups = {UserLoginValidGroup.class, UserEditValidGroup.class})
  private String account;

  /**
   * 密码
   */
  @NotNull(message = "密码不能为空", groups = {UserLoginValidGroup.class, UserEditValidGroup.class})
  private String password;

  /**
   * 昵称
   */
  @NotNull(message = "用户名不能为空", groups = {UserEditValidGroup.class})
  private String username;

  /**
   * 注册时间
   */
  @Column(name = "reg_time")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private LocalDateTime regTime;
}
