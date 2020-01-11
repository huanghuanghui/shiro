package com.hhh.shirospringboot.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hhh.shirospringboot.exception.CustomException;
import com.hhh.shirospringboot.exception.CustomUnauthorizedException;
import com.hhh.shirospringboot.model.UserDto;
import com.hhh.shirospringboot.model.common.BaseDto;
import com.hhh.shirospringboot.model.common.Constant;
import com.hhh.shirospringboot.model.common.ResponseBean;
import com.hhh.shirospringboot.model.valid.group.UserEditValidGroup;
import com.hhh.shirospringboot.model.valid.group.UserLoginValidGroup;
import com.hhh.shirospringboot.service.IUserService;
import com.hhh.shirospringboot.util.AesCipherUtil;
import com.hhh.shirospringboot.util.JwtUtil;
import com.hhh.shirospringboot.util.RedisHandle;
import com.hhh.shirospringboot.util.UserUtil;
import com.hhh.shirospringboot.util.common.StringUtil;
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
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hhh
 * @date 2020/1/10 9:10
 * @Despriction
 */
@RestController
@RequestMapping("/user")
@Log4j2
public class UserController {

  private final UserUtil userUtil;

  private IUserService userService;

  @Autowired
  public UserController(UserUtil userUtil, IUserService userService,RedisHandle redisHandle) {
    this.userUtil = userUtil;
    this.userService = userService;
  }

  /**
   * 获取用户列表
   */
  @GetMapping
  @RequiresPermissions(logical = Logical.AND, value = {"user:view"})
  public ResponseBean user(@Validated BaseDto baseDto) {
    if (baseDto.getPage() == null || baseDto.getRows() == null) {
      baseDto.setPage(1);
      baseDto.setRows(10);
    }
    PageHelper.startPage(baseDto.getPage(), baseDto.getRows());
    List<UserDto> userDtos = userService.selectAll();
    PageInfo<UserDto> selectPage = new PageInfo<UserDto>(userDtos);
    if (userDtos == null || userDtos.size() < 0) {
      throw new CustomException("查询失败(Query Failure)");
    }
    Map<String, Object> result = new HashMap<String, Object>(16);
    result.put("count", selectPage.getTotal());
    result.put("data", selectPage.getList());
    return new ResponseBean(HttpStatus.OK.value(), "查询成功(Query was successful)", result);
  }

  /**
   * 获取在线用户(查询Redis中的RefreshToken)
   */
  @GetMapping("/online")
  @RequiresPermissions(logical = Logical.AND, value = {"user:view"})
  public ResponseBean online() {
    List<Object> userDtos = new ArrayList<Object>();
    // 查询所有Redis键
    List<String> keys = RedisHandle.keys(Constant.PREFIX_SHIRO_REFRESH_TOKEN + "*");
    for (String key : keys) {
      if (RedisHandle.hasKey(key)) {
        // 根据:分割key，获取最后一个字符(帐号)
        String[] strArray = key.split(":");
        UserDto userDto = new UserDto();
        userDto.setAccount(strArray[strArray.length - 1]);
        userDto = userService.selectOne(userDto);
        // 设置登录时间
        userDto.setLoginTime(LocalDateTime.ofEpochSecond(Long.parseLong(RedisHandle.get(key).toString()),0, ZoneOffset.ofHours(8)));
        userDtos.add(userDto);
      }
    }
    if (userDtos == null || userDtos.size() < 0) {
      throw new CustomException("查询失败(Query Failure)");
    }
    return new ResponseBean(HttpStatus.OK.value(), "查询成功(Query was successful)", userDtos);
  }

  /**
   * 登录授权
   *
   * @param userDto
   */
  @PostMapping("/login")
  public ResponseBean login(@Validated(UserLoginValidGroup.class) @RequestBody UserDto userDto, HttpServletResponse httpServletResponse) {
    // 查询数据库中的帐号信息
    UserDto userDtoTemp = new UserDto();
    userDtoTemp.setAccount(userDto.getAccount());
    userDtoTemp = userService.selectOne(userDtoTemp);
    if (userDtoTemp == null) {
      throw new CustomUnauthorizedException("该帐号不存在(The account does not exist.)");
    }
    // 密码进行AES解密
    String key = AesCipherUtil.deCrypto(userDtoTemp.getPassword());
    // 因为密码加密是以账户+密码的形式进行加密的，所以解密后的对比是帐号+密码
    if (key.equals(userDto.getPassword())) {
      //清除可能存在的Shiro权限信息缓存 设置RefreshToken，时间戳为当前时间戳，直接设置即可(不用先删后设，会覆盖已有的RefreshToken)
      String currentTimeMillis = String.valueOf(System.currentTimeMillis());
      RedisHandle.set(Constant.PREFIX_SHIRO_REFRESH_TOKEN + userDtoTemp.getAccount(), currentTimeMillis, Constant.REFRESH_TOKEN_EXPIRE_TIME);
      // 从Header中Authorization返回AccessToken，时间戳为当前时间戳
      String token = JwtUtil.sign(userDtoTemp.getAccount(), currentTimeMillis);
      httpServletResponse.setHeader("Authorization", token);
      httpServletResponse.setHeader("Access-Control-Expose-Headers", "Authorization");
      log.info("用户登录-{}登录成功，token-{}", userDto.getAccount(),token);
      return new ResponseBean(HttpStatus.OK.value(), "登录成功(Login Success.)"+"", token);
    } else {
      throw new CustomUnauthorizedException("帐号或密码错误(Account or Password Error.)");
    }
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

  /**
   * 获取当前登录用户信息
   */
  @GetMapping("/info")
  @RequiresAuthentication
  public ResponseBean info() {
    // 获取当前登录用户
    UserDto userDto = userUtil.getUser();
    // 获取当前登录用户Id
    Integer id = userUtil.getUserId();
    // 获取当前登录用户Token
    String token = userUtil.getToken();
    // 获取当前登录用户Account
    String account = userUtil.getAccount();
    return new ResponseBean(HttpStatus.OK.value(), "您已经登录了(You are already logged in)", userDto);
  }

  /**
   * 获取指定用户
   */
  @GetMapping("/{id}")
  @RequiresPermissions(logical = Logical.AND, value = {"user:view"})
  public ResponseBean findById(@PathVariable("id") Integer id) {
    UserDto userDto = userService.selectByPrimaryKey(id);
    if (userDto == null) {
      throw new CustomException("查询失败(Query Failure)");
    }
    return new ResponseBean(HttpStatus.OK.value(), "查询成功(Query was successful)", userDto);
  }

  /**
   * 新增用户
   */
  @PostMapping
  @RequiresPermissions(logical = Logical.AND, value = {"user:edit"})
  public ResponseBean add(@Validated(UserEditValidGroup.class) @RequestBody UserDto userDto) {
    // 判断当前帐号是否存在
    UserDto userDtoTemp = new UserDto();
    userDtoTemp.setAccount(userDto.getAccount());
    userDtoTemp = userService.selectOne(userDtoTemp);
    if (userDtoTemp != null && StringUtil.isNotBlank(userDtoTemp.getPassword())) {
      throw new CustomUnauthorizedException("该帐号已存在(Account exist.)");
    }
    userDto.setRegTime(LocalDateTime.now());
    // 密码以用户名+密码的形式进行AES加密
    if (userDto.getPassword().length() > Constant.PASSWORD_MAX_LEN) {
      throw new CustomException("密码最多8位(Password up to 8 bits.)");
    }
    String key = AesCipherUtil.enCrypto(userDto.getPassword());
    userDto.setPassword(key);
    int count = userService.insert(userDto);
    if (count <= 0) {
      throw new CustomException("新增失败(Insert Failure)");
    }
    return new ResponseBean(HttpStatus.OK.value(), "新增成功(Insert Success)", userDto);
  }

  /**
   * 更新用户
   */
  @PutMapping
  @RequiresPermissions(logical = Logical.AND, value = {"user:edit"})
  public ResponseBean update(@Validated(UserEditValidGroup.class) @RequestBody UserDto userDto) {
    // 查询数据库密码
    UserDto userDtoTemp = new UserDto();
    userDtoTemp.setAccount(userDto.getAccount());
    userDtoTemp = userService.selectOne(userDtoTemp);
    if (userDtoTemp == null) {
      throw new CustomUnauthorizedException("该帐号不存在(Account not exist.)");
    } else {
      userDto.setId(userDtoTemp.getId());
    }
    // FIXME: 如果不一样就说明用户修改了密码，重新加密密码(这个处理不太好，但是没有想到好的处理方式)
    if (!userDtoTemp.getPassword().equals(userDto.getPassword())) {
      // 密码以帐号+密码的形式进行AES加密
      if (userDto.getPassword().length() > Constant.PASSWORD_MAX_LEN) {
        throw new CustomException("密码最多8位(Password up to 8 bits.)");
      }
      String key = AesCipherUtil.enCrypto(userDto.getAccount() + userDto.getPassword());
      userDto.setPassword(key);
    }
    int count = userService.updateByPrimaryKeySelective(userDto);
    if (count <= 0) {
      throw new CustomException("更新失败(Update Failure)");
    }
    return new ResponseBean(HttpStatus.OK.value(), "更新成功(Update Success)", userDto);
  }

  /**
   * 删除用户
   */
  @DeleteMapping("/{id}")
  @RequiresPermissions(logical = Logical.AND, value = {"user:edit"})
  public ResponseBean delete(@PathVariable("id") Integer id) {
    int count = userService.deleteByPrimaryKey(id);
    if (count <= 0) {
      throw new CustomException("删除失败，ID不存在(Deletion Failed. ID does not exist.)");
    }
    return new ResponseBean(HttpStatus.OK.value(), "删除成功(Delete Success)", null);
  }

  /**
   * 剔除在线用户
   */
  @DeleteMapping("/online/{id}")
  @RequiresPermissions(logical = Logical.AND, value = {"user:edit"})
  public ResponseBean deleteOnline(@PathVariable("id") Integer id) {
    UserDto userDto = userService.selectByPrimaryKey(id);
    RedisHandle.del(Constant.PREFIX_SHIRO_REFRESH_TOKEN + userDto.getAccount());
    return new ResponseBean(HttpStatus.OK.value(), "剔除成功(Delete Success)", null);
  }
}
