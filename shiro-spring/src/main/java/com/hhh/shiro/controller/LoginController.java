package com.hhh.shiro.controller;

import com.hhh.shiro.entity.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author hhh
 * @date 2020/1/9 10:05
 * @Despriction
 */
@Controller
public class LoginController {
  @RequestMapping(value = "/subLogin", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
  @ResponseBody
  public String subLogin(User user) {

    Subject subject = SecurityUtils.getSubject();
    UsernamePasswordToken token = new UsernamePasswordToken(user.getUsername(), user.getPassword());

    //设置shiro的记住我功能
    token.setRememberMe(user.getRememberMe());
    subject.login(token);

    if (subject.hasRole("超级管理员")) {
      return "有 admin 权限";
    }
    return "无 admin 权限";
  }
  @RequiresRoles("超级管理员")
  @GetMapping("/test/role")
  @ResponseBody
  public String testRole(){
    return "超级管理员，欢迎你";
  }
}
