package com.hhh.shirospringboot.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hhh.shirospringboot.exception.CustomException;
import com.hhh.shirospringboot.exception.CustomUnauthorizedException;
import com.hhh.shirospringboot.model.UserDto;
import com.hhh.shirospringboot.model.common.BaseDto;
import com.hhh.shirospringboot.model.common.Constant;
import com.hhh.shirospringboot.model.common.ResponseBean;
import com.hhh.shirospringboot.service.IUserService;
import com.hhh.shirospringboot.service.UserService;
import com.hhh.shirospringboot.util.AesCipherUtil;
import com.hhh.shirospringboot.util.JwtUtil;
import com.hhh.shirospringboot.util.RedisHandle;
import com.hhh.shirospringboot.util.UserUtil;
import com.hhh.shirospringboot.util.common.StringUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hhh
 * @date 2020/1/11 16:04
 * @Despriction
 */
@Service
@Log4j2
public class UserBaseServiceImpl implements UserService {

  private final UserUtil userUtil;

  private IUserService userService;

  @Autowired
  public UserBaseServiceImpl(UserUtil userUtil, IUserService userService) {
    this.userUtil = userUtil;
    this.userService = userService;
  }

  @Override
  public ResponseBean userList(BaseDto baseDto) {
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

  @Override
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

  @Override
  public ResponseBean login(UserDto userDto, HttpServletResponse httpServletResponse) {
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

  @Override
  public ResponseBean getCurrentUser() {
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

  @Override
  public ResponseBean getUser(Integer id) {
    UserDto userDto = userService.selectByPrimaryKey(id);
    if (userDto == null) {
      throw new CustomException("查询失败(Query Failure)");
    }
    return new ResponseBean(HttpStatus.OK.value(), "查询成功(Query was successful)", userDto);
  }

  @Override
  public ResponseBean addUser(UserDto userDto) {
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

  @Override
  public ResponseBean updateUser(UserDto userDto) {
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
    //移除本用户的本地缓存
    UserUtil.removeLru(userDto.getAccount());
    int count = userService.updateByPrimaryKeySelective(userDto);
    if (count <= 0) {
      throw new CustomException("更新失败(Update Failure)");
    }
    return new ResponseBean(HttpStatus.OK.value(), "更新成功(Update Success)", userDto);
  }

  @Override
  public ResponseBean deleteUser(Integer id) {
    UserDto userDto =userService.selectByPrimaryKey(id);
    if (userDto==null){
      throw new CustomException("删除失败，ID不存在(Deletion Failed. ID does not exist.)");
    }
    UserUtil.removeLru(userDto.getAccount());
    return new ResponseBean(HttpStatus.OK.value(), "删除成功(Delete Success)", userService.deleteByPrimaryKey(id));
  }

  @Override
  public ResponseBean deleteOnLine(Integer id) {
    UserDto userDto = userService.selectByPrimaryKey(id);
    RedisHandle.del(Constant.PREFIX_SHIRO_REFRESH_TOKEN + userDto.getAccount());
    return new ResponseBean(HttpStatus.OK.value(), "剔除成功(Delete Success)", null);
  }
}
