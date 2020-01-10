package com.hhh.shirospringboot.util;

import com.hhh.shirospringboot.exception.CustomException;
import com.hhh.shirospringboot.mapper.UserMapper;
import com.hhh.shirospringboot.model.UserDto;
import com.hhh.shirospringboot.model.common.Constant;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 获取当前登录用户工具类
 */
@Component
public class UserUtil {

  private final UserMapper userMapper;

  @Autowired
  public UserUtil(UserMapper userMapper) {
    this.userMapper = userMapper;
  }

  /**
   * 获取当前登录用户
   */
  public UserDto getUser() {
    String token = SecurityUtils.getSubject().getPrincipal().toString();
    // 解密获得Account
    String account = JwtUtil.getClaim(token, Constant.ACCOUNT);
    UserDto userDto = new UserDto();
    userDto.setAccount(account);
    userDto = userMapper.selectOne(userDto);
    // 用户是否存在
    if (userDto == null) {
      throw new CustomException("该帐号不存在(The account does not exist.)");
    }
    return userDto;
  }

  /**
   * 获取当前登录用户Id
   */
  public Integer getUserId() {
    return getUser().getId();
  }

  /**
   * 获取当前登录用户Token
   */
  public String getToken() {
    return SecurityUtils.getSubject().getPrincipal().toString();
  }

  /**
   * 获取当前登录用户Account
   */
  public String getAccount() {
    String token = SecurityUtils.getSubject().getPrincipal().toString();
    // 解密获得Account
    return JwtUtil.getClaim(token, Constant.ACCOUNT);
  }
}
