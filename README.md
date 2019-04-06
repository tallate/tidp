## TIdempotent

### [说明](http://tallate.github.io/d9c44370)

### 调试
1. 初始化中间件
使用`init`目录下的`run_mysql.sh`和`run_redis.sh`启动中间件（需要先安装**Docker**）。
如果是 MySQL，需要用`init_db.sql`初始化数据库。
1. 初始化配置
`tidp-test`中有一些测试代码。
  * 如果是 MySQL，在`resources/properties/mysql.properties`中设置配置项。
  * 如果是 Redis，在`resources/properties/redis.properties`中设置配置项。
1. 启动
因为测试代码是基于**SpringBoot**的，如果要使用一个中间件的客户端，只需要将对应的 starter 包作为依赖引入即可。
```xml
<!--如果测试MySQL，放开这里-->
<!--<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-jdbc</artifactId>
</dependency>-->

<!--如果测试Redis，放开这里-->
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```
SpringBoot 实际上是将配置转移到了离代码更近的地方，比如测试 MySQL 时可以使用`TestMySQLIdpApplication`启动，而测试 Redis 时可以使用`TestRedisIdpApplication`。
1. 测试
`tidp-test`的`test`目录下是一些简单的测试用例。

