package com.hhh.shirospringboot.util;

import com.hhh.shirospringboot.exception.CustomException;
import com.hhh.shirospringboot.mapper.UserMapper;
import com.hhh.shirospringboot.model.UserDto;
import com.hhh.shirospringboot.model.common.Constant;
import lombok.extern.log4j.Log4j2;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;

/**
 * 获取当前登录用户工具类
 */
@Component
@Log4j2
public class UserUtil {

  private final UserMapper userMapper;

  /**
   * 新建一个LinkedHashMap,实现Lru算法， 最近最久未使用的原则进行淘汰，将最近登录的用户信息缓存在本地JVM中，进行缓存双删，
   * 当更新与删除用户的时候，对队列中的用户进行删除，已达到减少访问数据库的作用
   * 不放在用户一进入的时候就存入，主要是考虑到本缓存是用来做活跃用户，当他登录后有进行其他操作的时候，在进行缓存
   */
  private static LinkedHashMap<String,UserDto> LRU_MAP = new Lru<String,UserDto>(1000);

  /**
   * LRU_MAP 移除本地JVM缓存中的用户
   * @param account
   */
  public static void removeLru(String account){
    log.info("JVM中移除用户-{}缓存",account);
    LRU_MAP.remove(account);
  }

  /**
   * 查看已登录用户中是否包含当前用户，若包含，可以不用再去数据库查询用户信息
   * @param account
   */
  public static Boolean contain(String account){
    Boolean exist =LRU_MAP.containsKey(account);
    log.info("JVM中查看用户-{}是否存在-{}",account,exist);
    return exist;
  }

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
    if (LRU_MAP.containsKey(account)){
      log.info("从当前JVM缓存中获取用户-{}",account);
      return LRU_MAP.get(account);
    }
    UserDto userDto = new UserDto();
    userDto.setAccount(account);
    userDto = userMapper.selectOne(userDto);
    // 用户是否存在
    if (userDto == null) {
      throw new CustomException("该帐号不存在(The account does not exist.)");
    }
    //放入JVM缓存
    LRU_MAP.put(account,userDto);
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
