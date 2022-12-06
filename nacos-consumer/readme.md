### 0. Feign与OpenFeign的对比

> OpenFeign是Spring Cloud在Feign的基础上支持了SpringMVC的注解，如@RequestMapping等等
>
> 使用上区别可见https://www.cnblogs.com/imyjy/p/16469625.html
>
> 所以在使用OpenFeign来进行微服务之间接口调用更好点
>

### 1. OpenFeign使用：

> 微服务调用首先要微服务要像注册中心进行注册，这里使用consul做注册中心

#### 1.1. 依赖：

```xml
<!--web-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

        <!--服务注册中心-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-consul-discovery</artifactId>
</dependency>

        <!--服务调用-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

#### 1.2. 提供者

##### 1.2.1. yml配置：

> 需要开启heartbeat配置，不然consul界面服务会显示All service checks failing，可能导致注册失败，然后feign就调用不到
> 便出现Load balancer does not have available server for client: consul-provider 错误

```yml
server:
  port: 8080

spring:
  application:
    name: consul-provider
  cloud:
    consul:
      host: localhost
      port: 8500
      discovery:
        serviceName: consul-provider
        heartbeat:
          enabled: true
```

##### 1.2.2. 启动类注解

```java

@SpringBootApplication
@EnableDiscoveryClient
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
```

##### 1.2.3. 提供者接口：

> com.example.demo.easy.controller.SysUserController.queryAll

```java

@RestController
@RequestMapping("sysUser")
public class SysUserController {

    @Resource
    private SysUserService sysUserService;

    @GetMapping("{id}")
    public ResponseEntity<SysUser> queryById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(sysUserService.queryById(id));
    }
}
```

#### 1.3. 消费者者

##### 1.3.1. yml配置：

> 需要开启heartbeat配置，不然consul界面服务会显示All service checks failing，可能导致注册失败，然后feign就调用不到
> 便出现Load balancer does not have available server for client: consul-provider 错误

```yml
server:
  port: 8081

spring:
  application:
    name: consul-consume
  cloud:
    consul:
      host: localhost
      port: 8500
      discovery:
        serviceName: consul-consume
        heartbeat:
          enabled: true
```

##### 1.3.2. 启动类：

> 需要添加@EnableDiscoveryClient 和 @EnableFeignClients 注解，

```java

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class ConsumeApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConsumeApplication.class, args);
    }
}
```

##### 1.3.3. feign接口定义：

> 使用@FeignClient注解，value为远程调用服务的serviceName，此处需要调用提供者接口，所以是提供者的serviceName
> 如果不一致可能会出现Load balancer does not have available server for client: consul-provider错误

```java

@FeignClient(value = "consul-provider")
public interface UserClientFeign {

    @RequestMapping("/sysUser/{id}")
    ResponseEntity<SysUser> queryById(@PathVariable("id") Long id);
}
```

##### 1.3.4. 消费者controller：

```java

@RestController
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping("/feign/{id}")
    public ResponseEntity<SysUser> feign(@PathVariable("id") Long id) {
        ResponseEntity<SysUser> sysUserResponseEntity = userService.queryById(id);
        return sysUserResponseEntity;
    }
}

@Service
public class UserService {

    @Autowired
    UserClientFeign userClientFeign;

    public ResponseEntity<SysUser> queryById(Long id) {
        return userClientFeign.queryById(id);
    }
}
```


#### 1.4. 配置中心

##### 1.4.0. 依赖：
```xml
<!--consul服务配置中心-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-consul-config</artifactId>
</dependency>
```

> consul还可以作为配置中心使用，yml文件要是bootstrap.yml
> bootstrap.yml的加载是先于application.yml的, 比如你一些数据库连接配置，放在了配置中心，那肯定得知道是哪个配置中心
> 所以配置中心的一些链接就要在bootstrap.yml中添加

##### 1.4.1. bootstrap.yml：
```yml
server:
  port: 8081

spring:
  application:
    name: consul-consume
  cloud:
    consul:
      host: localhost
      port: 8500
      discovery:
        serviceName: consul-consume
        heartbeat:
          enabled: true
        instance-id: ${spring.application.name}:${server.port}
      # 配置中心相关配置
      config:
        # 是否启用配置中心，默认值 true 开启
        enabled: true
        # 设置配置的基本文件夹，默认值 config 可以理解为配置文件所在的最外层文件夹
        prefix: config
        # 设置应用的文件夹名称，默认值 application
        default-context: consume
        # 指定配置格式为 yaml
        format: YAML
        # Consul 的 Key/Values 中的 Key，Value 对应整个配置文件
        data-key: dev
        # 以上配置可以理解为：加载 config/consume/ 文件夹下 Key 为 dev 的 Value 对应的配置信息

  #        watch:
#          # 是否开启自动刷新，默认值 true 开启
#          enabled: true
#          # 刷新频率，单位：毫秒，默认值 1000
#          delay: 1000
```
![image](/img/Snipaste_2022-12-05_10-33-09.png)


##### 1.4.2. 接口读取配置：

> @Value 读取配置值
> @RefreshScope 可以动态刷新，即配置更新会被感知到
```java
@Service
@RefreshScope
public class UserService {

    @Autowired
    UserClientFeign userClientFeign;

    @Value("${name}")
    String name;

    public ResponseEntity<SysUser> queryById(Long id) {
        System.out.println("name: " + name);
        return userClientFeign.queryById(id);
    }
}

```

#### 1.5. OpenFeign调用原理

> 可以查考链接进行学习：
> https://www.cnblogs.com/lay2017/p/11946324.html
> https://zhuanlan.zhihu.com/p/457256778
> https://www.bilibili.com/video/BV16W4y1H7tw/
> 大致流程如下：
> - 启动类注解@EnableFeignClients，引入了@Import(FeignClientsRegistrar.class)
> - FeignClientsRegistrar的registerBeanDefinitions方法默认扫描启动类所在包下有@FeignClient标注的类
> - 然后生成各自FeignClientFactoryBean的BeanDefinition，然后进行BeanDefinition的注册（org.springframework.beans.factory.support.DefaultListableBeanFactory.registerBeanDefinition）
> - 在bean生命周期时，对@FeignClient标注的bean进行生成代理对象，代理对象持有Dispatch，包含了Method和MethodHandler的映射关系
> -（
> - 比如当UserService注入userClientFeign，由于userClientFeign被封装为FeignClientFactoryBean，是一个FactoryBean，
> - 所以获取比如userClientFeign实例对象，会触发FeignClientFactoryBean的getObject方法生成userClientFeign的代理对象
> - 构建proxy对象的时候会构建Method和MethodHandler的映射关系，MethodHandler的默认实现类是SynchronousMethodHandler
> - ）
> - UserService注入userClientFeign是个代理对象，当发起请求时，通过代理对象，找到持有的MethodHandler，默认会触发SynchronousMethodHandler的invoke方法，进行http请求
> - 一开始的url是http://consul-provider/xxxx/sysUser/1这样，经过负载均衡找到对应的server，将url替换为http://192.168.0.104:8080/xxxx/sysUser/1
> - 然后就是请求响应了
>

![image](/img/v2-aef43332eb037786f6b32e2a301088f2_1440w.webp)
![image](/img/v2-caa487501d26e8ac7a55551eca160fc3_1440w.webp)
![image](/img/Snipaste_2022-12-05_22-21-38.png)
