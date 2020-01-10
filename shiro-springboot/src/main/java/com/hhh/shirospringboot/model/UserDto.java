package com.hhh.shirospringboot.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hhh.shirospringboot.model.entity.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Table;
import javax.persistence.Transient;
import java.time.LocalDateTime;

@Table(name = "user")
@Getter
@Setter
public class UserDto extends User {
  /**
   * 登录时间
   */
  @Transient
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private LocalDateTime loginTime;
}
