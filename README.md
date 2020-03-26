## 前言
在基于Spring boot/cloud的微服务项目中，要创建若干module，每个module都要依赖Redis、mybatis和一些基础工具类等等，本项目就是为了简化Spring boot的配置，在自己项目中提炼出来的公共服务配置和工具，简化大型项目的开发依赖;
无论是基于Spring boot的单体应用架构还是基于Spring Cloud的微服务应用架构，均提供模块化支持。


### Ultron
本项目命名SpringUltron，灵感来自复仇者联盟2:奥创纪元，奥创是一个强大的人工智能机器人，希望本项目越来越强大，基于更多的自动化配置和抽象，能在项目开发中节约更多的时间；
按功能拆分了N多模块，可以按需依赖，减少打包代码


[![996.icu](https://img.shields.io/badge/link-996.icu-red.svg)](https://996.icu)
[![License](https://img.shields.io/badge/apache-2.0-blue.svg?style=flat)](http://www.apache.org/licenses/ "Feel free to contribute.")

## 项目结构
```shell
*├── spring-ultron                  项目父级目录
    ├── spring-ultron-dependencies  依赖版本统一管理
    ├── ultron-core                 核心库(请求统一返回体、常用错误代码、自定义业务异常、Jackson序列化/反序列化配置、常用工具类等)
    ├── ultron-crypto               对称及非对称加密解密工具，实现了:AES、DES、RSA、国密SM2、SM4等；以及各种秘钥生成工具
    ├── ultron-mybatis              mybatis plus自动化配置、分页工具等
    ├── ultron-redis                Redis自动化配置、操作客户端
    ├── ultron-boot                 Spring boot脚手架，servlet/reactive全局异常捕获、基于aop的注解API日志打印(支持配置文件配置日志开关，日志内容等)、WebClient http客户端封装
    ├── ultron-cloud                Spring cloud脚手架（基于Spring Cloud Alibaba）
    ├── ultron-http                 基于OKhttp3 4.0.0版本封装的http客户端，Fluent语法风格，使用非常简便
    ├── ultron-security             Spring Security通用配置，支持jwt登录鉴权，RBAC权限控制
    ├── ultron-swagger              Swagger文档自动化配置(可在配置文件中开启/关闭，支持http basic认证)
```    

## 使用步骤

### 第一步，下载本项目

    git clone https://github.com/brucewuu520/spring-ultron.git
    
### 第二步，编译安装本项目

    mvn clean install --项目会编译安装到本地maven仓库
    
### 第三步，在自己的工程按需添加依赖库

1、在项目parent pom.xml中添加：

    <properties>
        <spring-ultron.version>2.0.0</spring-ultron.version>
        <java.version>1.8</java.version>
    </properties>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springultron</groupId>
                <artifactId>spring-ultron-dependencies</artifactId>
                <version>${spring-ultron.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

2、核心库(请求统一返回体、常用错误代码、自定义业务异常、Jackson序列化/反序列化配置、常用工具类等)

    <dependency>
       <groupId>org.springultron</groupId>
       <artifactId>ultron-core</artifactId>
    </dependency>
    
    统一返回体使用示例：
        @GetMapping("/test")
        public Result<Test>> test() {
            Test test = new Test();
            return Result.success(test); // or return Result.failed(ResultCode.PARAM_VALID_FAILED);
        }
        
    自定义异常使用：
        @GetMapping("/test")
        public Result<Test>> test() {
            return ApiResult.throwFail(ResultCode.API_EXCEPTION);
        }
    自定义错误状态码枚举类实现IResultCode接口，即可使用 ApiResult.fail(IResultCode)返回错误信息
    
    Jackson配置序列化和反序列化支持java8 Time
    
        
3、ultron-crypto 对称及非对称加密解密工具，实现了:AES、DES、RSA、国密SM2、SM4等；以及各种秘钥生成工具

        <dependency>
           <groupId>org.springultron</groupId>
           <artifactId>ultron-mybatis</artifactId>
        </dependency>
    
    部分加解密算法实现（如：国密SM2、SM4）需添加BC库依赖：
    
        <dependency>
            <groupId>org.bouncycastle</groupId>
            <artifactId>bcprov-jdk15on</artifactId>
            <version>${bouncycastle.version}</version>
        </dependency>
    
    使用示例见单元测试        
        
4、mybatis plus自动化配置、分页工具等

    <dependency>
       <groupId>org.springultron</groupId>
       <artifactId>ultron-mybatis</artifactId>
    </dependency> 
    
    分页工具使用示例：
    
        PageQuery query = new PageQuery();
        query.setCurrent(1);
        query.setSize(10);
        String[] asces = new String[] {"sort", "order"};
        query.setAscs(asces);
        IPage<T> page = PageUtils.getPage(query);
 
    
5、Redis自动化配置、操作客户端

    <dependency>
       <groupId>org.springultron</groupId>
       <artifactId>ultron-redis</artifactId>
    </dependency>
    
    Redis操作客户端使用示例:
    
        @Autowired
        private RedisClient redisClient;
     
        redisClient.setString("key", "value", Duration.ofSeconds(120))
        
        Object obj = new Object();
        redisClient.set("key", obj, Duration.ofSeconds(120))
        
        ...
        
    Spring cache 扩展cache name 支持 # 号分隔 cache name 和 超时 ttl(单位秒)。使用示例：
    
        @CachePut(value = "user#300", key = "#id")
    
6、Spring boot脚手架，servlet/全局异常捕获、基于aop的注解API日志打印(支持配置文件配置日志开关，日志内容等)、WebClient http客户端封装

    <dependency>
       <groupId>org.springultron</groupId>
       <artifactId>ultron-boot</artifactId>
    </dependency>
    
    请求日志使用示例(不支持reactive运行环境):
        @ApiLog(description = "用户登录")
        @GetMapping("/login")
        public Result<User>> login() {
            UserDTO userDTO = new UserDTO();
            ...
            return Result.success(userDTO, "登录成功");
        }
        
    请求日志配置（配置文件添加）:
        ultron:
          log:
            enable: true # 开启ApiLog打印
            level: headers # 打印包括请求头 none/body
            
    WebClient http客户端使用示例：
    需添加依赖：
    
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-webflux</artifactId>
        </dependency>
     
    使用示例：
        
        Map<String, Object> params = Maps.newHashMap(3);
        params.put("id", 108);
        params.put("name", "张三");
        params.put("age", 23);
        Mono<JSONObject> mono = WebClientUtil.postJSON("https://xxx", params, JSONObject.class)
        
    异步任务线程池配置：
        
        ultron:
          async:
            core-pool-size: 3         # 核心线程数，默认：3
            max-pool-size: 300        # 线程池最大数量，默认：300  
            queue-capacity: 30000     # 线程池队列容量，默认：30000
            keep-alive-seconds: 300   # 空闲线程存活时间，默认：300秒
    
7、Spring cloud脚手架(基于Spring cloud alibaba 2.2.0)

    <dependency>
       <groupId>org.springultron</groupId>
       <artifactId>ultron-cloud</artifactId>
    </dependency>
    
    默认集成了sentinel熔断限流，支持传统servlet和reactive，统一配置熔断/限流的异常返回值
    
    负载均衡的http客户端使用（基于ReactorLoadBalancerExchangeFilterFunction的反应式负载均衡器，性能比RestTemplate要好得多）:
    
        @Autowire
        private WebClient lbWebClient;           

8、Swagger 接口文档

    <dependency>
       <groupId>org.springultron</groupId>
       <artifactId>ultron-swagger</artifactId>
    </dependency> 
    
    文档配置(配置文件中添加):
    
        swagger:
          enable: true  # 默认
          title: xxx服务
          description: xxx服务接口文档
          contact-user: brucewuu
          contact-email: xxx@xxx.com
          contact-url: xxx
          
9、Spring Security通用配置，支持jwt登录鉴权，RBAC权限控制

    <dependency>
        <groupId>org.springultron</groupId>
        <artifactId>ultron-security</artifactId>
    </dependency> 
                
10、基于OkHttp3 4.x版本使用示例

    <properties>
        <okhttp3.version>4.4.0</okhttp3.version>
    </properties>
    
    <dependency>
        <groupId>org.springultron</groupId>
        <artifactId>ultron-http</artifactId>
    </dependency>  

    使用示例(同步)：
       JSONObject result = HttpRequest.get("https://xxx")
                  .query("name1", "value1")
                  .query("name2", "value2")
                  .log()
                  .execute()
                  .asObject(JSONObject.class);
       
       Map<String, Object> params = Maps.newHashMap(3);
               params.put("id", 108);
               params.put("name", "张三");
               params.put("age", 23);           
       Map<String, Object> result = HttpRequest.post("https://xxx")
                         .bodyJson(params)
                         .log()
                         .execute()
                         .asMap();
                         
    异步示例：
    
       HttpRequest.get("https://xxx")
             .query("name1", "value1")
             .query("name2", "value2")
             .enqueue(new Callback() {
                 @Override
                 public void onFailure(@NotNull Call call, @NotNull IOException e) {
                     
                 }
     
                 @Override
                 public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
     
                 })

    
## 更新日志
* 2.0版本基于Spring boot 2.2.x
* 1.0版基于Spring boot 2.1.x 请切换分支1.x

## 鸣谢
感谢 [Mica](https://github.com/lets-mica/mica)，有些工具类和配置参考了mica            
                
## 许可证

[Apache License 2.0](https://github.com/brucewuu520/spring-ultron/blob/master/LICENSE)

Copyright (c) 2019-2020 brucewuu    