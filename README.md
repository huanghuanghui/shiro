# Shiro安全框架学习

锚点：

1. [什么是Shiro](#什么是Shiro)
2. [与SpringSecurity比较](#与SpringSecurity比较)
3. [整体架构图](#整体架构图)
4. [Shiro认证](#Shiro认证)
5. [Shiro授权](#Shiro授权)

## 什么是Shiro

- Apache的强大灵活的开源安全框架
- 认证、授权、企业回话管理、安全加密

## 与SpringSecurity比较

1.Shiro

- 简单、灵活
- 可脱离Spring
- 粒度较粗

2.SpringSecurity

- 复杂、笨重
- 不可脱离Spring
- 粒度更细

## 整体架构图

![img](http://jn-hhh.oss-cn-hangzhou.aliyuncs.com/450630682335af9ee2e1a4b66803418.jpg)

Shiro通过SecurityManager提供安全服务，管理着其他组件的实例。

- `Authenticator`认证器：管理着登录登出
- `Authorizer`:授权器，赋予主体（用户）拥有的权限
- `SessionManager`：Shiro实现的Session管理器，可以在不借助web容器的情况下使用Session。
- `Session Dao`:提供Session的操作，Session的增删改查
- `Cache Manager`:缓存管理器，可以缓存角色数据与权限数据
- `Realm`:数据库与Shiro之间的桥梁，认证信息、权限数据都是通过Realm来获取（通过自定义Realm）
- `Cryptography`:数据加密

## Shiro认证

创建SecurityManager->主体（用户）提交认证->SecurityManager认证->Authenticator->认证->Realm认证

```java
public class AuthenticationTest {
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
```

## Shiro授权

创建SecurityManager->主体（用户）授权->SecurityManager授权->Authorizer授权->Realm获取角色权限数据
