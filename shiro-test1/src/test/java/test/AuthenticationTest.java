package test;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.SimpleAccountRealm;
import org.apache.shiro.subject.Subject;
import org.junit.Before;
import org.junit.Test;

/**
 * @author hhh
 * @date 2020/1/8 10:20
 * @Despriction
 */
public class AuthenticationTest {
  public static void main(String[] args) {

  }

  SimpleAccountRealm simpleAccountRealm = new SimpleAccountRealm();

  @Before
  public void addSimpleAccountRealm(){
    simpleAccountRealm.addAccount("mark","123456","admin","saa_super");
  }
  @Test
  public void testAuthentication(){
    //构建Security Manager 环境
    DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();
    defaultSecurityManager.setRealm(simpleAccountRealm);//设置realm
    SecurityUtils.setSecurityManager(defaultSecurityManager);//Shiro 环境注册
    Subject subject = SecurityUtils.getSubject();//主体提交认证情求
    UsernamePasswordToken token = new UsernamePasswordToken("mark","123456");
    //登录->授权
    subject.login(token);
    System.out.println("login isAuthenticated? "+subject.isAuthenticated());//判断是否认证
    subject.checkRoles("admin","saa_super");//检查用户是否具备admin角色 (角色认证)
    subject.logout();//退出
    System.out.println("logout isAuthenticated? "+subject.isAuthenticated());
  }
}
