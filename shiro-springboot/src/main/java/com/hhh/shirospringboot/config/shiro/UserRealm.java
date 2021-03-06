package com.hhh.shirospringboot.config.shiro;

import com.hhh.shirospringboot.config.shiro.jwt.JwtToken;
import com.hhh.shirospringboot.mapper.PermissionMapper;
import com.hhh.shirospringboot.mapper.RoleMapper;
import com.hhh.shirospringboot.mapper.UserMapper;
import com.hhh.shirospringboot.model.PermissionDto;
import com.hhh.shirospringboot.model.RoleDto;
import com.hhh.shirospringboot.model.UserDto;
import com.hhh.shirospringboot.model.common.Constant;
import com.hhh.shirospringboot.util.JwtUtil;
import com.hhh.shirospringboot.util.RedisHandle;
import com.hhh.shirospringboot.util.UserUtil;
import com.hhh.shirospringboot.util.common.StringUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author hhh
 * @date 2020/1/10 9:10
 * @Despriction 自定义Realm
 */
@Service
@Log4j2
public class UserRealm extends AuthorizingRealm {

  private final UserMapper userMapper;
  private final RoleMapper roleMapper;
  private final PermissionMapper permissionMapper;

  @Autowired
  public UserRealm(UserMapper userMapper, RoleMapper roleMapper, PermissionMapper permissionMapper) {
    this.userMapper = userMapper;
    this.roleMapper = roleMapper;
    this.permissionMapper = permissionMapper;
  }

  /**
   * 大坑，必须重写此方法，不然Shiro会报错
   */
  @Override
  public boolean supports(AuthenticationToken authenticationToken) {
    return authenticationToken instanceof JwtToken;
  }

  /**
   * 获取授权信息
   * 只有当需要检测用户权限的时候才会调用此方法，例如checkRole,checkPermission之类的
   */
  @Override
  protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
    SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
    String account = JwtUtil.getClaim(principalCollection.toString(), Constant.ACCOUNT);
    UserDto userDto = new UserDto();
    userDto.setAccount(account);
    // 查询用户角色
    List<RoleDto> roleDtos = roleMapper.findRoleByUser(userDto);
    for (RoleDto roleDto : roleDtos) {
      if (roleDto != null) {
        // 添加角色
        simpleAuthorizationInfo.addRole(roleDto.getName());
        // 根据用户角色查询权限
        List<PermissionDto> permissionDtos = permissionMapper.findPermissionByRole(roleDto);
        for (PermissionDto permissionDto : permissionDtos) {
          if (permissionDto != null) {
            // 添加权限
            simpleAuthorizationInfo.addStringPermission(permissionDto.getPerCode());
          }
        }
      }
    }
    return simpleAuthorizationInfo;
  }

  /**
   * 获取身份验证信息
   * 默认使用此方法进行用户名正确与否验证，错误抛出异常即可。
   */
  @Override
  protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
    String token = (String) authenticationToken.getCredentials();
    // 解密获得account，用于和数据库进行对比
    String account = JwtUtil.getClaim(token, Constant.ACCOUNT);
    // 帐号为空
    if (StringUtil.isBlank(account)) {
      throw new AuthenticationException("Token中帐号为空(The account in Token is empty.)");
    }
    //校验token是否过期
    verifyToken(token,account);
    //当前的活跃用户中，包含当前用户，直接过验证，不情求数据库
    if(UserUtil.contain(account)){
      log.info("当前的活跃用户中，包含当前用户-{}，直接过验证，不情求数据库",account);
      return new SimpleAuthenticationInfo(token, token, "userRealm");
    }
    // 查询用户是否存在
    UserDto userDto = new UserDto();
    userDto.setAccount(account);
    userDto = userMapper.selectOne(userDto);
    if (userDto == null) {
      throw new AuthenticationException("该帐号不存在(The account does not exist.)");
    }
    // 获取RefreshToken的时间戳
    String currentTimeMillisRedis = RedisHandle.get(Constant.PREFIX_SHIRO_REFRESH_TOKEN + account).toString();
    // 获取AccessToken时间戳，与RefreshToken的时间戳对比
    if (JwtUtil.getClaim(token, Constant.CURRENT_TIME_MILLIS).equals(currentTimeMillisRedis)) {
      return new SimpleAuthenticationInfo(token, token, "userRealm");
    }
    throw new AuthenticationException("Token已过期(Token expired or incorrect.)");
  }

  /**
   * 验证token是否过期
   * @param token
   * @param account
   * @return
   */
  private Boolean verifyToken(String token,String account){
    //开始认证，要AccessToken认证通过，且Redis中存在RefreshToken，且两个Token时间戳一致
    if (JwtUtil.verify(token) && RedisHandle.hasKey(Constant.PREFIX_SHIRO_REFRESH_TOKEN + account)){
      return Boolean.TRUE;
    }
    throw new AuthenticationException("Token已过期(Token expired or incorrect.)");
  }
}
