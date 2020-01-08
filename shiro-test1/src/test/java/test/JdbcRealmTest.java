package test;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.jdbc.JdbcRealm;
import org.apache.shiro.subject.Subject;
import org.junit.Test;

/**
 * @author hhh
 * @date 2020/1/8 11:23
 * @Despriction
 */
public class JdbcRealmTest {

  DruidDataSource druidDataSource =new DruidDataSource();
  {
    druidDataSource.setUrl("jdbc:mysql://192.168.1.34:3306/ssr_test_20191207?useUnicode=true&characterEncoding=utf8&autoReconnect=true&useSSL=false");
    druidDataSource.setUsername("root");
    druidDataSource.setPassword("mysql#JN99@dev");
  }

  @Test
  public void testAuthentication() {
    // 映入 JdbcRealm
    JdbcRealm jdbcRealm = new JdbcRealm();
    //设置数据源
    jdbcRealm.setDataSource(druidDataSource);
    //使用jdbcRealm需要设置权限的开关，默认为false否则可能获取权限失败
    jdbcRealm.setPermissionsLookupEnabled(true);
    //使用自己的SQL语句进行身份验证授权
    String sql = "select password from sy_user_info where login_name = ?";
    jdbcRealm.setAuthenticationQuery(sql);
    //查询角色信息
    String roleSql = "select s.role_name from sy_user_role_relation r join sy_user_info u on u.id = r.user_id join sy_role s on r.role_id = s.id  where u.login_name = ?";
    jdbcRealm.setUserRolesQuery(roleSql);
    //查询权限信息
    String authority = "select a.name\n" +
      "from sy_auth a\n" +
      "       join sy_auth_role_relation ar on ar.auth_id = a.id\n" +
      "       join sy_user_role_relation ur on ar.role_id = ur.role_id\n" +
      "       join sy_role role on role.id = ur.role_id\n" +
      "where  role_name= ?";
    jdbcRealm.setPermissionsQuery(authority);
    //构建security manager 环境
    DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();
    defaultSecurityManager.setRealm(jdbcRealm);
    SecurityUtils.setSecurityManager(defaultSecurityManager);
    Subject subject = SecurityUtils.getSubject();
    subject.login(new UsernamePasswordToken("saa-1111", "bPUVFb0AtIADZ5eFfyH1QA=="));
    //检查是否认证
    System.out.println("isAuthenticated:" + subject.isAuthenticated());
    //检查角色
    subject.checkRoles("客服调度","网络运营经理","SAA平台--超管");
    subject.checkPermission("救援管理");
  }
}
