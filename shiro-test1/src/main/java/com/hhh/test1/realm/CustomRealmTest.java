package com.hhh.test1.realm;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.subject.Subject;

/**
 * @author hhh
 * @date 2020/1/8 15:32
 * @Despriction
 */
public class CustomRealmTest {
  public static void main(String[] args) {
    CustomRealm customRealm = new CustomRealm();
    //构建security manager 环境
    DefaultSecurityManager defaultSecurityManager  = new DefaultSecurityManager();
    defaultSecurityManager.setRealm(customRealm);

    //在Realm中设置加密算法，加密次数，对密码进行加密，需要在认证时，确定其加密算法
    //密码使用MD5进行加密
    HashedCredentialsMatcher matcher = new HashedCredentialsMatcher();
    matcher.setHashIterations(1);
    //设置加密算法
    matcher.setHashAlgorithmName("md5");
    customRealm.setCredentialsMatcher(matcher);

    SecurityUtils.setSecurityManager(defaultSecurityManager);
    Subject subject =SecurityUtils.getSubject();
    subject.login(new UsernamePasswordToken("saa-1111","123456"));
    System.out.println("isAuthenticated:"+subject.isAuthenticated());
    //角色
    subject.checkRoles("超级管理员");
    //是否具备权限
    subject.checkPermissions("删除用户","查询用户");
  }
}
