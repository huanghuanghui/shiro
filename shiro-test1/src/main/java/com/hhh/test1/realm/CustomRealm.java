package com.hhh.test1.realm;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

import java.util.HashMap;
import java.util.HashSet;

/**
 * @author hhh
 * @date 2020/1/8 15:16
 * @Despriction
 */
public class CustomRealm extends AuthorizingRealm {

  private static final HashMap<String, String> USER_TOKEN_MAP = new HashMap<String, String>();
  private static final HashMap<String, HashSet<String>> USER_ROLE_MAP = new HashMap<String, HashSet<String>>();
  private static final HashSet<String> ROLE_SET = new HashSet<>();
  private static final HashMap<String, HashSet<String>> USER_PERMISSION_MAP = new HashMap<String, HashSet<String>>();
  private static final HashSet<String> PERMISSION_SET = new HashSet<>();

  static {
    //md5加密
    USER_TOKEN_MAP.put("saa-1111", "d4be99eea3ac515b1db341214daee4cc");

    ROLE_SET.add("超级管理员");
    ROLE_SET.add("普通用户");
    USER_ROLE_MAP.put("saa-1111", ROLE_SET);

    PERMISSION_SET.add("添加用户");
    PERMISSION_SET.add("删除用户");
    PERMISSION_SET.add("修改用户");
    PERMISSION_SET.add("查询用户");
    USER_PERMISSION_MAP.put("saa-1111", PERMISSION_SET);
  }

  /**
   * 认证接口
   *
   * @param token 主体传输的认证信息
   * @return
   * @throws AuthenticationException
   */
  @Override
  protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
    //从主体传输的认证信息中获取用户名
    String username = (String) token.getPrincipal();
    //使用用户名去数据库中获取凭证
    String password = getPasswordByUserName(username);
    if (password == null) {
      return null;
    }
    SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo(username, password, "customRealm");
    simpleAuthenticationInfo.setCredentialsSalt(ByteSource.Util.bytes("huanghuanghui"));
    return simpleAuthenticationInfo;
  }

  /**
   * 授权
   *
   * @param principals
   * @return
   */
  @Override
  protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
    //从认证信息中获取用户
    String username = (String) principals.getPrimaryPrincipal();
    //通过用户获取角色数据
    HashSet<String> roles = getRoleByUserName(username);
    //通过用户名获取权限数据
    HashSet<String> permissions = getPermissionsByUserName(username);
    SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
    simpleAuthorizationInfo.setStringPermissions(permissions);
    simpleAuthorizationInfo.setRoles(roles);
    return simpleAuthorizationInfo;
  }

  public String getPasswordByUserName(String username) {
    return USER_TOKEN_MAP.get(username);
  }

  public HashSet<String> getRoleByUserName(String username) {
    return USER_ROLE_MAP.get(username);
  }

  public HashSet<String> getPermissionsByUserName(String username) {
    return USER_PERMISSION_MAP.get(username);
  }

  public static void main(String[] args) {
    //加密
    Md5Hash md5Hash = new Md5Hash("123456","huanghuanghui");
    System.out.println(md5Hash.toString());
  }
}
