package test;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.text.IniRealm;
import org.apache.shiro.subject.Subject;
import org.junit.Test;

/**
 * @author hhh
 * @date 2020/1/8 10:52
 * @Despriction
 */
public class IniRealmTest {
  @Test
  public void testAuthentication(){
    IniRealm iniRealm = new IniRealm("classpath:user.ini");
    //构建security manager 环境
    DefaultSecurityManager defaultSecurityManager  = new DefaultSecurityManager();
    defaultSecurityManager.setRealm(iniRealm);
    SecurityUtils.setSecurityManager(defaultSecurityManager);
    Subject subject =SecurityUtils.getSubject();
    subject.login(new UsernamePasswordToken("mark","123456"));
    System.out.println("isAuthenticated:"+subject.isAuthenticated());
    //角色
    subject.checkRole("admin");
    //是否具备权限
    subject.checkPermission("user:delete");
  }
}
