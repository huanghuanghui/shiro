#### 项目介绍

1. RESTful API
2. Shiro + Java-JWT实现无状态鉴权机制(Token)
3. 密码加密(采用AES-128 + Base64的方式)
4. 集成Redis(lettuce)
5. 重写Shiro缓存机制(Redis)
6. Redis中保存RefreshToken信息(做到JWT的可控性)
7. 根据RefreshToken自动刷新AccessToken

##### 关于Shiro + Java-JWT实现无状态鉴权机制(Token)

> 1. 首先**Post**用户名与密码到**user/login**登入，成功返回加密的**AccessToken**，失败直接返回401错误(帐号或密码不正确)
> 2. 以后访问都带上这个**AccessToken**即可
> 3. 鉴权流程主要是重写了**Shiro**的入口过滤器**JWTFilter**(**BasicHttpAuthenticationFilter**)，判断请求**Header**里面是否包含**Authorization**字段
> 4. 有就进行**Shiro**的**Token**登录认证授权(用户访问每一个需要权限的请求必须在**Header**中添加**Authorization**字段存放**AccessToken**)，没有就以游客直接访问(有权限管控的话，以游客访问就会被拦截)

##### 关于AES-128 + Base64当两个用户的明文密码相同时进行加密，会发现数据库中存在相同结构的暗文密码

> 大部分是以**MD5 + 盐**的形式解决了这个问题(详细自己百度)，我采用**AES-128 + Base64**是以帐号+密码的形式进行加密密码，因为帐号具有唯一性，所以也不会出现相同结构的暗文密码这个问题

##### 关于将lettuce与SpringBoot整合

> 本来是直接将**lettuce的RedisTemplate**注入为**Bean**，直接静态注入RedisHandle类中，提供静态调用Redis操作方法

##### 关于Redis中保存RefreshToken信息(做到JWT的可控性)

> 1. 登录认证通过后返回**AccessToken**信息(在**AccessToken**中**保存当前的时间戳和帐号**)
> 2. 同时在**Redis**中设置一条以**帐号为Key，Value为当前时间戳(登录时间)\**的\**RefreshToken**
> 3. 现在认证时必须**AccessToken**没失效以及**Redis**存在所对应的**RefreshToken**，且**RefreshToken时间戳**和**AccessToken信息中时间戳一致**才算认证通过，这样可以做到**JWT的可控性**
> 4. 如果重新登录获取了新的**AccessToken**，旧的**AccessToken**就认证不了，因为**Redis**中所存放的的**RefreshToken时间戳信息**只会和最新生成的**AccessToken信息中携带的时间戳一致**，这样每个用户就只能使用最新的**AccessToken**认证
> 5. **Redis**的**RefreshToken**也可以用来判断用户是否在线，如果删除**Redis**的某个**RefreshToken**，那这个**RefreshToken**所对应的**AccessToken**之后也无法通过认证了，就相当于控制了用户的登录，可以剔除用户

##### 关于根据RefreshToken自动刷新AccessToken

> 1. 本身**AccessToken的过期时间为5分钟**(配置文件可配置)，**RefreshToken过期时间为30分钟**(配置文件可配置)
> 2. 当登录后时间过了5分钟之后，当前**AccessToken**便会过期失效，再次带上**AccessToken**访问**JWT**会抛出**TokenExpiredException**异常说明**Token**过期
> 3. 开始判断是否要**进行AccessToken刷新**，**Redis查询当前用户的RefreshToken是否存在**，**以及这个RefreshToken所携带时间戳**和**过期AccessToken所携带的时间戳**是否**一致**
> 4. **如果存在且一致就进行AccessToken刷新，设置过期时间为5分钟(配置文件可配置)，时间戳为当前最新时间戳，同时也设置RefreshToken中的时间戳为当前最新时间戳，刷新过期时间重新为30分钟过期(配置文件可配置)**
> 5. 最终将刷新的**AccessToken**存放在**Response的Header中的Authorization字段**返回(前端进行获取替换，下次用新的**AccessToken**进行访问)

#### 软件架构

1. SpringBoot + Mybatis核心框架
2. PageHelper插件 + 通用Mapper插件
3. Shiro + Java-JWT无状态鉴权认证机制
4. Redis(lettuce)缓存框架
5. 引入Log4j2日志框架，替代spring默认logging日志框架，可以自己修改默认的配置文件log4j2-spring.xml中的文件输出路径

#### 登录方式

![image-20200111160010576](http://jn-hhh.oss-cn-hangzhou.aliyuncs.com/image-20200111160010576.png)

#### 通过header中的Jwt验证用户信息

header中添加 Authorization ：

![image-20200111160016725](http://jn-hhh.oss-cn-hangzhou.aliyuncs.com/image-20200111160016725.png)
