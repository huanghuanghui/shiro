package com.hhh.shirospringboot.controller;

import com.hhh.shirospringboot.model.UserDto;
import com.hhh.shirospringboot.model.common.BaseDto;
import com.hhh.shirospringboot.model.common.ResponseBean;
import com.hhh.shirospringboot.model.valid.group.UserEditValidGroup;
import com.hhh.shirospringboot.model.valid.group.UserLoginValidGroup;
import com.hhh.shirospringboot.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * @author hhh
 * @date 2020/1/10 9:10
 * @Despriction
 */
@RestController
@RequestMapping("/user")
@Log4j2
public class UserController {

  private final UserService userService;

  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }

  /**
   * 获取用户列表
   */
  @GetMapping
  @RequiresPermissions(logical = Logical.AND, value = {"user:view"})
  public ResponseBean user(@Validated BaseDto baseDto) {
    return userService.userList(baseDto);
  }

  /**
   * 获取在线用户(查询Redis中的RefreshToken)
   */
  @GetMapping("/online")
  @RequiresPermissions(logical = Logical.AND, value = {"user:view"})
  public ResponseBean online() {
    return userService.online();
  }

  /**
   * 登录授权
   *
   * @param userDto
   */
  @PostMapping("/login")
  public ResponseBean login(@Validated(UserLoginValidGroup.class) @RequestBody UserDto userDto, HttpServletResponse httpServletResponse) {
    return userService.login(userDto, httpServletResponse);
  }

  /**
   * 获取当前登录用户信息
   */
  @GetMapping("/info")
  @RequiresAuthentication
  public ResponseBean info() {
    return userService.getCurrentUser();
  }

  /**
   * 获取指定用户
   */
  @GetMapping("/{id}")
  @RequiresPermissions(logical = Logical.AND, value = {"user:view"})
  public ResponseBean findById(@PathVariable("id") Integer id) {
    return userService.getUser(id);
  }

  /**
   * 新增用户
   */
  @PostMapping
  @RequiresPermissions(logical = Logical.AND, value = {"user:edit"})
  public ResponseBean add(@Validated(UserEditValidGroup.class) @RequestBody UserDto userDto) {
    return userService.addUser(userDto);
  }

  /**
   * 更新用户
   */
  @PutMapping("/update")
  @RequiresPermissions(logical = Logical.AND, value = {"user:edit"})
  public ResponseBean update(@Validated(UserEditValidGroup.class) @RequestBody UserDto userDto) {
    return userService.updateUser(userDto);
  }

  /**
   * 删除用户
   */
  @DeleteMapping("/{id}")
  @RequiresPermissions(logical = Logical.AND, value = {"user:edit"})
  public ResponseBean delete(@PathVariable("id") Integer id) {
    return userService.deleteUser(id);
  }

  /**
   * 剔除在线用户
   */
  @DeleteMapping("/online/{id}")
  @RequiresPermissions(logical = Logical.AND, value = {"user:edit"})
  public ResponseBean deleteOnline(@PathVariable("id") Integer id) {
    return userService.deleteOnLine(id);
  }

  /**
   * 测试登录
   */
  @GetMapping("/article")
  public ResponseBean article() {
    Subject subject = SecurityUtils.getSubject();
    // 登录了返回true
    if (subject.isAuthenticated()) {
      return new ResponseBean(HttpStatus.OK.value(), "您已经登录了(You are already logged in)", null);
    } else {
      return new ResponseBean(HttpStatus.OK.value(), "你是游客(You are guest)", null);
    }
  }

  /**
   * 测试登录注解(@RequiresAuthentication和subject.isAuthenticated()返回true一个性质)
   */
  @GetMapping("/article2")
  @RequiresAuthentication
  public ResponseBean requireAuth() {
    return new ResponseBean(HttpStatus.OK.value(), "您已经登录了(You are already logged in)", null);
  }
}
