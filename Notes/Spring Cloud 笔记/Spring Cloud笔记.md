[toc]

## 1、微服务架构编码 构建

![image](https://user-images.githubusercontent.com/50070756/125549363-0f8a4bcf-5bf4-471c-8c42-a1a1c2836215.png)

 ==约定>配置>编码==

## 2、Eureka服务注册发现

### 1、基础知识

##### 服务治理

* Spring Cloud封装了Netflix 公司开发的Eureka模块来实现==服务治理==
  在传统的rpc远程调用框架中，管理每个服务与服务之间依赖关系比较复杂，管理比较复杂，所以需要使用服务治理，管理服务于
  服务之间依赖关系，可以实现服务调用、负载均衡、容错等，实现服务发现与注册。

##### 服务注册 

* Eureka采用了CS的设计架构，Eureka Senver作为服务注册功能的服务器，它是服务注册中心。而系统中的其他微被务，使用Eureka的客户嫦连接到lEureka Server并维持心跳连接。这样系统的维护人员就可以通过Eureka Server来监控系统中各个微服务是否正常运行。
  在服务注册与发现中，有一个注册中心。当服务器启动的时候，会把当前自己服务器的信息比如服务地址通讯地址等以别名方式注册到注册中心上。另一方(消费者服务提供
  者)，以该别名的方式去注册中心上获取到实际的服务通讯地址，然后再实现本地RPC调用RPC远程调用框架核心设计思想:在于注册中心，因为使用注册中心管理每个服务与
  服务之间的一个依赖关系(服务治理概念)。在任何rpc远程框架中，都会有一个注册中心(存放服务地址相关信息(接口地址)

![image](https://user-images.githubusercontent.com/50070756/125549390-99c7e0d3-2447-4d57-953c-4787ccb18c54.png)

##### Eureka两组件

Eureka包含两个组件:==Eureka Server==和==Eureka Client==

##### Eureka Server提供服务注册服务

* 各个微服务节点通过配置启动后，会在EurekaServer中进行注册，这样EurekaServer中的服务注册表中将会存储所有可用服务节点的信息，服务节点的信息可以在界面中直观看到。

##### EurekaClient通过注册中心进行访问

* 是一个Java客户端，用于简化Eureka Server的交互，客户端同时也具备一个内置的、使用轮询(round-robin)负载算法的负载均衡器
  。在应用启动后，将会向Eureka Server发送心跳(默认周期为30秒)。如果Eureka Server在多个心跳周期内没有接收到某个节点的心
  跳，EurekaServer将会从服务注册表中把这个服务节点移除（默认90秒)

### 2、单机Eureka搭建

##### 搭建EurekaServer端服务注册中心

1. 搭建 **cloud-eureka-server7001** 模块

2. 改POM

   ```xml 
   pom
    <artifactId>cloud-eureka-server7001</artifactId>
       <dependencies>
           <!--eureka-server-->
           <dependency>
               <groupId>org.springframework.cloud</groupId>
               <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
           </dependency>
           <!-- 引入自己定义的api通用包，可以使用Payment支付Entity -->
           <dependency>
               <groupId>com.sanqi.cloud</groupId>
               <artifactId>cloud-api-common</artifactId>
               <version>${project.version}</version>
           </dependency>
           <!--boot web actuator-->
           <dependency>
               <groupId>org.springframework.boot</groupId>
               <artifactId>spring-boot-starter-web</artifactId>
           </dependency>
           <dependency>
               <groupId>org.springframework.boot</groupId>
               <artifactId>spring-boot-starter-actuator</artifactId>
           </dependency>
           <!--一般通用配置-->
           <dependency>
               <groupId>org.springframework.boot</groupId>
               <artifactId>spring-boot-devtools</artifactId>
               <scope>runtime</scope>
               <optional>true</optional>
           </dependency>
           <dependency>
               <groupId>org.projectlombok</groupId>
               <artifactId>lombok</artifactId>
           </dependency>
           <dependency>
               <groupId>org.springframework.boot</groupId>
               <artifactId>spring-boot-starter-test</artifactId>
               <scope>test</scope>
           </dependency>
           <dependency>
               <groupId>junit</groupId>
               <artifactId>junit</artifactId>
           </dependency>
       </dependencies>
   
   </project>
   ```

3. 写YML

   ``` yaml
   server:
     port: 7001
   #spring:
     #application:
       #name: cloud-eureka-service
   
   eureka:
     instance:
       hostname: eureka7001.com #eureka服务端的实例名称
     client:
       register-with-eureka: false     #false表示不向注册中心注册自己。
       fetch-registry: false     #false表示自己端就是注册中心，我的职责就是维护服务实例，并不需要去检索服务
       service-url:
       #集群指向其它eureka
         #defaultZone: http://eureka7002.com:7002/eureka/
       #单机就是7001自己
         defaultZone: http://eureka7001.com:7001/eureka/
     #server:
       #关闭自我保护机制，保证不可用服务被及时踢除
       #enable-self-preservation: false
       #eviction-interval-timer-in-ms: 2000
   ```

   

4. 主启动

   ``` java
   import org.springframework.boot.SpringApplication;
   import org.springframework.boot.autoconfigure.SpringBootApplication;
   import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
   
   /**
    * Created by 三七 on 2020/7/22.
    */
   @SpringBootApplication
   @EnableEurekaServer
   public class EurekaMain7001
   {
       public static void main(String[] args) {
           SpringApplication.run(EurekaMain7001.class, args);
       }
   }
   
   ```

5. 测试

   http://localhost:7001/

6. 测试界面

   ![image](https://user-images.githubusercontent.com/50070756/125549328-efd7e54f-6313-468c-bcaf-4133c6e1eb52.png)

* No application available没有服务被发现  因为没有注册服务进来当前不可能有服务被发现

#####  搭建 EurekaClient端，将注册进EurekaServer成为服务提供者provider

1. 创建 cloud-provider-payment8001 服务提供者模块

2. 改POM

   ``` xml
    <artifactId>cloud-provider-payment8001</artifactId>
   
       <dependencies>
           <!--包含了sleuth+zipkin-->
           <dependency>
               <groupId>org.springframework.cloud</groupId>
               <artifactId>spring-cloud-starter-zipkin</artifactId>
           </dependency>
           <!--eureka-client-->
           <dependency>
               <groupId>org.springframework.cloud</groupId>
               <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
           </dependency>
           <dependency>
               <groupId>org.springframework.boot</groupId>
               <artifactId>spring-boot-starter-web</artifactId>
           </dependency>
           <dependency>
               <groupId>org.springframework.boot</groupId>
               <artifactId>spring-boot-starter-actuator</artifactId>
           </dependency>
           <dependency>
               <groupId>org.mybatis.spring.boot</groupId>
               <artifactId>mybatis-spring-boot-starter</artifactId>
           </dependency>
           <dependency>
               <groupId>com.alibaba</groupId>
               <artifactId>druid-spring-boot-starter</artifactId>
               <version>1.1.10</version>
           </dependency>
           <!--mysql-connector-java-->
           <dependency>
               <groupId>mysql</groupId>
               <artifactId>mysql-connector-java</artifactId>
           </dependency>
           <!--jdbc-->
           <dependency>
               <groupId>org.springframework.boot</groupId>
               <artifactId>spring-boot-starter-jdbc</artifactId>
           </dependency>
           <dependency>
               <groupId>org.springframework.boot</groupId>
               <artifactId>spring-boot-devtools</artifactId>
               <scope>runtime</scope>
               <optional>true</optional>
           </dependency>
           <dependency>
               <groupId>org.projectlombok</groupId>
               <artifactId>lombok</artifactId>
               <optional>true</optional>
           </dependency>
           <dependency>
               <groupId>org.springframework.boot</groupId>
               <artifactId>spring-boot-starter-test</artifactId>
               <scope>test</scope>
           </dependency>
   
   
           <dependency>
               <groupId>com.sanqi.cloud</groupId>
               <artifactId>cloud-api-common</artifactId>
               <version>${project.version}</version>
           </dependency>
       </dependencies>
   
   </project>
   
   ```

3. 写YML

   ``` yaml
   server:
     port: 8001
   
   spring:
     application:
       name: cloud-payment-service
     datasource:
       type: com.alibaba.druid.pool.DruidDataSource            # 当前数据源操作类型
       driver-class-name: org.gjt.mm.mysql.Driver              # mysql驱动包
       url: jdbc:mysql://localhost:3306/dbSpringCloud?useUnicode=true&characterEncoding=utf-8&useSSL=false
       username: root
       password: root
   eureka:
     client:
       #表示是否将自己注册进EurekaServer默认为true。
       register-with-eureka: true
       #是否从EurekaServer抓取已有的注册信息，默认为true。单节点无所谓，集群必须设置为true才能配合ribbon使用负载均衡
       fetchRegistry: true
       service-url:
         #单机版
         defaultZone: http://localhost:7001/eureka
         #集群版
         #defaultZone: http://eureka7001.com:7001/eureka,http://eureka7002.com:7002/eureka
     instance:
       instance-id: payment8001
       prefer-ip-address: true
   
   mybatis:
     mapper-locations: classpath:mapper/*.xml
     type-aliases-package: com.san.springcloud.entities  #所有entity别名所在包
   ```

4. 主启动

   ```java
   import org.springframework.boot.SpringApplication;
   import org.springframework.boot.autoconfigure.SpringBootApplication;
   import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
   import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
   
   /**
    * Created by 三七 on 2020/7/16.
    */
   @SpringBootApplication
   @EnableEurekaClient
   @EnableDiscoveryClient
   public class PaymentMain8001
   {
       public static void main(String[] args) {
           SpringApplication.run(PaymentMain8001.class, args);
       }
   }
   ```

5. 测试

   ==先启动EurekaServer服务==

​       **注册名启动配置说明**

![image](https://user-images.githubusercontent.com/50070756/125549428-d3253ca0-ea78-45c5-a639-6d7627b168ac.png)

​     **自我保护机制**



![image](https://user-images.githubusercontent.com/50070756/125549546-7e82667e-69ca-474b-b898-b5dcce6d9755.png)

### 3、Euekar集群搭建

##### Eureka集群原理说明

![image](https://user-images.githubusercontent.com/50070756/125549699-b13e691e-42c9-436d-8f0f-f23a59a8f2d4.png)==解决办法==：<font color="#FF0000" > 搭建Eureka注册中心集群,实现负载均衡+故障容错</font>

##### 负载均衡

* 使用<font color="#FF0000" > @LoadBalanced</font>注解赋予<font color="#FF0000" > RestTemplate</font>负载均衡的能力

  ``` java
  import org.springframework.cloud.client.loadbalancer.LoadBalanced;
  import org.springframework.context.annotation.Bean;
  import org.springframework.context.annotation.Configuration;
  import org.springframework.web.client.RestTemplate;
  
  /**
   * Created by 三七 on 2020/7/21.
   */
  @Configuration
  public class ApplicationContextConfig
  {
      @Bean
      @LoadBalanced
      public RestTemplate getRestTemplate()
      {
          return new RestTemplate();
      }
  }
  
  ```

### 4、服务发现Discovery

* 对于注册eureka里面的微服务,可以通过服务发现来获得该服务的信息
* @EnableDiscoveryClient 在 Cloud-provider-payment8001 主启动类上加此注解

### 5、eureka自我保护

##### 1、故障现象

* 概述
  保护模式主要用于一组客户端和Eureka Server之间存在网络分区场景下的保护。一旦进入保护模式,
  <font color="#FF0000" >Eureka Server将会尝试保护其服务注册表中的信息，不再删除服务注册表中的数据，也就是不会注销任何微服务。</font>
* 如果在Eureka Server的首页看到以下这段提示，则说明Eureka进入了保护模式:
  EMERGENCY!EUREKA MAY BE INCORRECTLY CLAIMING INSTANCES ARE UP WHEN THEY'RE NOT.
  RENEWALS ARE LESSER THAN THRESHOLD AND HENCETHEINSTANCES ARE NOTBEINGEXPIREDJUSTTO BE SAFE

##### 2、导致原因

* ==为什么会产生Eureka自我保护机制?==
  为了防止EurekaClient可以正常运行，但是与EurekaServer网络不通情况下，EurekaServer不会立刻将EurekaClient服务剔除

* ==什么是自我保护模式?==
  默认情况下，如果EurekaServer在一定时间内没有接收到某个微服务实例的心跳，EurekaServer将会注销该实例(默认90秒)。但
  是当网络分区故障发生(延时、卡顿、拥挤)时，微服务与EurekaServer之间无法正常通信，以上行为可能变得非常危险了——因为微
  服务本身其实是健康的，此时本不应该注销这个微服务。Eureka通过“自我保护模式”来解决这个问题——当EurekaServer节点在
  短时间内丢失过多客户端时(可能发生了网络分区故障)，那么这个节点就会进入自我保护模式。

![image](https://user-images.githubusercontent.com/50070756/125549553-19f95d14-b064-4946-b12c-7d3866b56127.png)

* 它的设计哲学就是宁可保留错误的服务注册信息，也不盲自注销任何可能健康的服务实例。—句话讲解∶好死不如赖活着
  综上，自我保护模式是一种应对网络异常的安全保护措施。它的架构哲学是宁可同时保留所有微服务（健康的微服务和不健康的微服
  务都会保留）也不盲目注销任何健康的微服务。使用自我保护模式，可以让Eureka集群更加的健壮、稳定。

  

<font color="#FF0000" >一句话:某时刻 一个微服务不可用了,Eureka不会立刻清理,依旧会对该服务的信息进行保存</font>

## 3、Consul服务注册与发现

### 1、简介

##### 是什么

* Consul是一套开源的分布式服务发现和配置管理系统，由HashiCorp公司用Go语言开发。
  提供了微服务系统中的服务治理、配置中心、控制总线等功能。这些功能中的每一个都可以根据需要单独使用，也可以一起使用以构建全方位的服务网格，总之Consul提供了—种完整的服务网格解决方案。

##### 用处

* 服务发现：提供HTTP/DNS两种发现方式
* 健康检测：支持多种方式,HTTP、TCP、Docker、shell脚本定制化
* kv存储：Key、Value的存储方式
* 多数据中心：Consul支持多数据中心

##### 学习

* https://www.springcloud.cc/spring-cloud-consul.html

##### 官网下载

* https://learn.hashicorp.com/consul/getting-started/install.html

##### 启动开发者模式

* consul agent -dev

### 2、服务提供者

1. 新建Module支付服务，cloud-providerconsul-payment8006

2. 改POM

   ``` xml
     <artifactId>cloud-providerconsul-payment8006</artifactId>
   
   
       <dependencies>
           <!--SpringCloud consul-server-->
           <dependency>
               <groupId>org.springframework.cloud</groupId>
               <artifactId>spring-cloud-starter-consul-discovery</artifactId>
           </dependency>
           <dependency>
               <groupId>com.sanqi.cloud</groupId>
               <artifactId>cloud-api-common</artifactId>
               <version>${project.version}</version>
           </dependency>
           <dependency>
               <groupId>org.springframework.boot</groupId>
               <artifactId>spring-boot-starter-web</artifactId>
           </dependency>
           <dependency>
               <groupId>org.springframework.boot</groupId>
               <artifactId>spring-boot-starter-actuator</artifactId>
           </dependency>
           <dependency>
               <groupId>org.springframework.boot</groupId>
               <artifactId>spring-boot-devtools</artifactId>
               <scope>runtime</scope>
               <optional>true</optional>
           </dependency>
           <dependency>
               <groupId>org.projectlombok</groupId>
               <artifactId>lombok</artifactId>
               <optional>true</optional>
           </dependency>
           <dependency>
               <groupId>org.springframework.boot</groupId>
               <artifactId>spring-boot-starter-test</artifactId>
               <scope>test</scope>
           </dependency>
       </dependencies>
   </project>
   
   ```

3. 写YML

   ``` yaml
   server:
     # consul服务端口
     port: 8006
   spring:
     application:
       name: cloud-provider-payment
     cloud:
       consul:
         # consul注册中心地址
         host: localhost
         port: 8500
         discovery:
           hostname: 127.0.0.1
           service-name: ${spring.application.name}
   ```

4. 主启动

   ```java
   import org.springframework.boot.SpringApplication;
   import org.springframework.boot.autoconfigure.SpringBootApplication;
   import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
   
   /**
    * Created by 三七 on 2020/8/7.
    */
   @SpringBootApplication
   @EnableDiscoveryClient
   public class PaymentMain8006 {
       public static void main(String[] args) {
           SpringApplication.run(PaymentMain8006.class,args);
       }
   }
   ```

5. 业务类Controller

   ``` java
   import cn.hutool.core.lang.UUID;
   import lombok.extern.slf4j.Slf4j;
   import org.springframework.beans.factory.annotation.Value;
   import org.springframework.web.bind.annotation.RequestMapping;
   import org.springframework.web.bind.annotation.RestController;
   
   /**
    * Created by 三七 on 2020/8/7.
    */
   @RestController
   @Slf4j
   public class PaymentController {
       @Value("${server.port}")
       private String serverPort;
   
       @RequestMapping(value = "payment/consul")
       public String paymentConsul() {
           return "SpringCloud with consul:" + serverPort + "\t" + UUID.randomUUID().toString();
       }
   }
   ```

6. 测试

   http://localhost:8006/payment/consul

### 3、服务消费者

1. 新建Module消费服务，cloud-consumerconsul-order80

2. 改POM

   ``` xml
     <artifactId>cloud-consumerconsul-order80</artifactId>
       <dependencies>
           <!--SpringCloud consul-server-->
           <dependency>
               <groupId>org.springframework.cloud</groupId>
               <artifactId>spring-cloud-starter-consul-discovery</artifactId>
           </dependency>
           <dependency>
               <groupId>com.sanqi.cloud</groupId>
               <artifactId>cloud-api-common</artifactId>
               <version>${project.version}</version>
           </dependency>
           <dependency>
               <groupId>org.springframework.boot</groupId>
               <artifactId>spring-boot-starter-web</artifactId>
           </dependency>
           <!--监控-->
           <dependency>
               <groupId>org.springframework.boot</groupId>
               <artifactId>spring-boot-starter-actuator</artifactId>
           </dependency>
           <!--热部署-->
           <dependency>
               <groupId>org.springframework.boot</groupId>
               <artifactId>spring-boot-devtools</artifactId>
               <scope>runtime</scope>
               <optional>true</optional>
           </dependency>
           <dependency>
               <groupId>org.projectlombok</groupId>
               <artifactId>lombok</artifactId>
               <optional>true</optional>
           </dependency>
           <dependency>
               <groupId>org.springframework.boot</groupId>
               <artifactId>spring-boot-starter-test</artifactId>
               <scope>test</scope>
           </dependency>
       </dependencies>
   </project>
   
   ```

3. 写YML

   ``` yaml
   server:
     port: 80
   spring:
     application:
       name: cloud-consumer-order
     cloud:
       consul:
         # consul注册中心地址
         host: localhost
         port: 8500
         discovery:
           hostname: 127.0.0.1
           service-name: ${spring.application.name}
   ```

4. 主启动

   ``` java
   import org.springframework.boot.SpringApplication;
   import org.springframework.boot.autoconfigure.SpringBootApplication;
   import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
   
   /**
    * Created by 三七 on 2020/8/7.
    */
   @SpringBootApplication
   @EnableDiscoveryClient  //该注解用于向使用consul或者zookeeper作为注册中心时注册服务
   public class OrderConsulMain80 {
       public static void main(String[] args) {
           SpringApplication.run(OrderConsulMain80.class,args);
       }
   }
   ```

5. 配置bean

   ``` java
   import org.springframework.cloud.client.loadbalancer.LoadBalanced;
   import org.springframework.context.annotation.Bean;
   import org.springframework.context.annotation.Configuration;
   import org.springframework.web.client.RestTemplate;
   
   /**
    * Created by 三七 on 2020/8/7.
    */
   @Configuration
   public class ApplicationContextConfig
   {
       @Bean
       @LoadBalanced
       public RestTemplate getRestTemplate()
       {
           return new RestTemplate();
       }
   }
   ```

6. 测试

   http://localhost/consumer/payment/consul

### 4、三种注册中心的异同点

##### CAP(分区容错性要保证,所以要么是CP,要么是AP)

* C: Consistency(强一致性)
* A: Availability(可用性)
* P: Parttition tolerance(分区容错性)
* CAP理论关注粒度是否是数据,而不是整体系统设计的策略

##### 经典CAP图

​	![image](https://user-images.githubusercontent.com/50070756/125549762-a4d06cfe-e686-43ac-9f96-94e8a9a30a8a.png)

* <font color="#FF0000" >最多只能同时较好的满足两个。</font>
  CAP理论的核心是:<font color="#FF0000" >一个分布式系统不可能同时很好的满足一致性，可用性和分区容错性这三个需求，</font>
  因此，根据CAP原理将NoSQL数据库分成了满足CA原则、满足CP原则和满足AP原则三大类:
  CA-单点集群，满足一致性，可用性的系统，通常在可扩展性上不太强大。
  CP-满足—致性，分区容忍必的系统，通常性能不是特别高。
  AP-满足可用性，分区容忍性的系统，通常可能对—致性要求低一些。

##### AP(eureka)

​			![image](https://user-images.githubusercontent.com/50070756/125549787-b38e1b4d-fe1c-4e65-9f26-d685d2e22b4b.png)

##### CP(Zookeeper/Consul)

* CP架构
  当网络分区出现后,为了保证一致性,就必须拒绝请求,否则无法保证一致性
  <font color="#FF0000" >结论:违背了可用性A的要求,只满足一致性和分区容错,即CP</font>

​			![image](https://user-images.githubusercontent.com/50070756/125549809-f282ef8c-7c5b-4cf7-95b4-27db4b06d6fb.png)

## 4、Ribbon负载均衡调用

### 1、概述

##### 是什么

* Spring Cloud Ribbon是基于Netflix Ribbon实现的一套<font color="#FF0000" >客户端	负载均衡的工具。</font>
  简单的说，Ribbon是Netflix发布的开源项目，主要功能是提供<font color="#FF0000" >客户端的软件负载均衡算法和服务调用。</font>Ribbon客户端组件提供一系列完善的配置项如连接超时，重试等。简单的说，就是在配置文件中列出Load Balancer(简称LB)后面所有的机器，Ribbon会自动的帮助你基于某种规则(如简单轮询，随机连接等）去连接这些机器。我们很容易使用Ribbon实现自定义的负载均衡算法。
* LB(负载均衡 Load Balance)
  简单的说就是将用户的请求平摊的分配到多个服务上，从而达到系统的HA(高可用）。
  常见的负载均衡有软件Nginx，LVS，硬件F5等。
* Ribbon本地负载均衡客户端与Nginx服务端负载均衡区别
  Nginx是服务器负载均衡，客户端所有请求都会交给nginx，然后由nginx实现转发请求。即负载均衡是由服务端实现的。
  Ribbon本地负载均衡，在调用微服务接口时候，会在注册中心上获取注册信息服务列表之后缓存到VM本地，从而在本地实现RPC远
  程服务调用技术。

##### 用途

* 集中式LB
  即在服务的消费方和提供方之间使用独立的LB设施(可以是硬件，如F5,也可以是软件，如nginx)由该设施负责把访问请求通过某种策
  咯转发至服务的提供方;

* 进程内LB
  将LB逻辑集成到消费方，消费方从服务注册中心获知有哪些地址可用，然后自己再从这些地址中选择出一个合适的服务器。
  Ribbon就属于进程内LB，它只是一个类库，集成于消费方进程，消费方通过它来获取到服务提供方的地址。

  <font color="#FF0000" >负载均衡+RestTemplate调用</font>

### 2、架构说明

​				![image](https://user-images.githubusercontent.com/50070756/125549837-74601f03-1f1c-4578-91f7-47c386c45c0a.png)

* Ribbon在工作时分成两步
  第一步先选择EurekaServer ,它优先选择在同一个区域内负载较少的server.
  第二步再根据用户指定的策略，在从server取到的服务注册列表中选择一个地址。
  其中Ribbon提供了多种策略:比如轮询、随机和根据响应时间加权。

### 3、Ribbon核心组件IRule

##### IRule:根据特定算法从服务列表中选取一个要访问的服务

* com.netflix.loadbalancer.RoundRobinRule 	轮询

* com.netflix.loadbalancer.RandomRule             随机

* com.netflix.loadbalancer.RetryRule      

  先按照RoundRobinRule的策略获取服务,如果获取服务失败则在指定时间内进行重试,获取可用的服务    

* WeightedResponseTimeRule      

  对RoundRobinRule的扩展,响应速度越快的实例选择权重越多大,越容易被选择

* BestAvailableRule     

  会先过滤掉由于多次访问故障而处于断路器跳闸状态的服务,然后选择一个并发量最小的服务

* AvailabilityFilteringRule

  先过滤掉故障实例,再选择并发较小的实例

* ZoneAvoidanceRule

  默认规则,复合判断server所在区域的性能和server的可用性选择服务器

##### 替换

1. 新建包           com.xxx.myrule

2. 上面包下新建MySelfRule规则类

   ``` java
   package com.san.myrule;
   
   import com.netflix.loadbalancer.IRule;
   import com.netflix.loadbalancer.RandomRule;
   import org.springframework.context.annotation.Bean;
   import org.springframework.context.annotation.Configuration;
   
   /**
    * Created by 三七 on 2020/8/9.
    */
   @Configuration
   public class MySelfRule {
   
       @Bean
       public IRule myRule() {
           // 定义为随机
           return new RandomRule();
       }
   }
   
   ```

3. 主启动类添加@RibbonClient

   ``` java
   package com.san.springcloud;
   
   import com.san.myrule.MySelfRule;
   import org.springframework.boot.SpringApplication;
   import org.springframework.boot.autoconfigure.SpringBootApplication;
   
   import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
   import org.springframework.cloud.netflix.ribbon.RibbonClient;
   
   
   /**
    * Created by 三七 on 2020/7/21.
    */
   @SpringBootApplication
   @EnableEurekaClient
   @RibbonClient(name = "CLOUD-PAYMENT-SERVICE",configuration=MySelfRule.class)
   public class OrderMain80 {
       public static void main(String[] args) {
           SpringApplication.run(OrderMain80.class, args);
       }
   }
   ```

### 3、Ribbon负载均衡算法

##### 原理

<font color="#FF0000">负载均衡算法：rest接口第几次请求数 % 服务器集群总数量 = 实际调用服务器位置下标，每次服务启动后rest接口计数从1开始</font>

##### 源码

![img](C:\Users\13294\AppData\Local\YNote\data\qq1ECA3E69B3F57F19F3D4544949F1D060\3c186999cf0a4e52b7073b26de77cb2f\clipboard.png)

![img](C:\Users\13294\AppData\Local\YNote\data\qq1ECA3E69B3F57F19F3D4544949F1D060\2884ad8134384c7c91c0a388ce892e7f\clipboard.png)

![img](C:\Users\13294\AppData\Local\YNote\data\qq1ECA3E69B3F57F19F3D4544949F1D060\2f9dbc315d2740a18ad2fccd37c0fdc8\clipboard.png)

![img](C:\Users\13294\AppData\Local\YNote\data\qq1ECA3E69B3F57F19F3D4544949F1D060\6a1482d3c80e4a079356c1b39493d069\clipboard.png)

![img](C:\Users\13294\AppData\Local\YNote\data\qq1ECA3E69B3F57F19F3D4544949F1D060\3966ee3541e145d4aecf198102373174\clipboard.png)

##### 自定义

``` java
import org.springframework.cloud.client.ServiceInstance;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by 三七 on 2020/8/9.
 */
public class MyLB implements LoadBalancer {

    private AtomicInteger atomicInteger = new AtomicInteger(0);

    private  final int incrementAndGet(){
        int current;
        int next;
        do {
            current = this.atomicInteger.get();
            next = current >= 2147483647 ? 0 : current + 1;
        }while (!this.atomicInteger.compareAndSet(current,next));
        System.out.println("*****第几次访问，次数next: "+next);
        return next;
    }

    //负载均衡算法：rest接口第几次请求数 % 服务器集群总数量 = 实际调用服务器位置下标，每次服务启动后rest接口计数从1开始
    @Override
    public ServiceInstance instances(List<ServiceInstance> serviceInstances) {
        int index =incrementAndGet() % serviceInstances.size();
        return serviceInstances.get(index);
    }

}
```



## 5、OpenFeign服务接口调用

## 6、Hystrix熔断器

## 7、Gateway新一代网关

#### 1、概述
##### 是什么
* Gateway是在Spring生态系统之上构建的API网关服务，基于Spring 5，Spring Boot 2和Project Reactor等技术。
* Gateway旨在提供一种简单有效的方式来对API进行路由，以及提供一些强大的过滤器功能，例如：熔断、限流、重试等。
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200817170031115.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQ0NzE3Mzc2,size_16,color_FFFFFF,t_70#pic_center)
#### Spring cloud Gateway
* SpringCloud Gateway是Spring Cloud的一个全新项目,基于Spring 5.0+ Spring Boot 2.0和Project Reactor等技术开发的网关,它旨在为微服务架构提供一种简单有效的统一的API路由管理方式。

**注：**  ==SpringCloud Gateway使用的是Webflux中的reactor-netty响应式编程组件,底层使用了Netty通讯框架。==

##### 能干什么
* 反向代理
* 鉴权
* 流量监控
* 熔断
* 日志监控
* ················
#### 2、三大核心概念
* **Route(路由)**  路由是构建网关的基本模块,它由ID,目标URI,一系列的断言和过滤器组成,如断言为true则匹配该路由。
* **Predicate(断言)**   开发人员可以匹配HTTP请求中的所有内容(例如请求头或请求参数),如果请求与断言相匹配则进行路由。
* **Filter(过滤)** 指的是Spring框架中GatewayFilter的实例,使用过滤器,可以在请求被路由前或者之后对请求进行修改。
**总结：**
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200817171641112.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQ0NzE3Mzc2,size_16,color_FFFFFF,t_70#pic_center)
**web请求，通过一些匹配条件，定位到真正的服务节点，并在这个转发过程的前后，进行一些精细化控制；predicate就是我们的匹配条件；而filter就可以理解为一个无所不能的拦截器，有了这两个元素，子啊家上uri，就可以实现一个具体的路由了**
#### 3、Gateway工作流程
*官网介绍*
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200817172359453.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQ0NzE3Mzc2,size_16,color_FFFFFF,t_70#pic_center)
* 客户端向Spring Cloud Gateway发出请求。然后在Gateway Handler Mapping中找到与请求相匹配的路由，将其发送到GatewayWeb Handler。
* Handler再通过指定的过滤器链来将请求发送到我们实际的服务执行业务逻辑，然后返回。
* 过滤器之间用虚线分开是因为过滤器可能会在发送代理请求之前( "pre" )或之后( "post" )执行业务逻辑。
* Filter在"pre" 类型的过滤器可以做参数校验、权限校验、流量监控、日志输出、协议转换等,
* 在"post" 类型的过滤器中可以做响应内容、响应头的修改，日志的输出，流量监控等有着非常重要的作用。
* <font color="#FF0000" >核心逻辑：</font> 路由转发+执行过滤器链。

## 7、SpringCloud config分布式配置中心

## 8、SpringCloud Bus消息总线

## 9、SpringCloud Stream消息驱动

## 10、SpringCloud Sleuth分布式链路跟踪

## 11、SpringCloud Alibaba Nacos服务注册和配置中心

## 12、SpringCloud Alibaba Sentinel实现熔断与限流

#### 1、流控规则

资源名:唯一名称，默认请求路径
针对来源: Sentinel可以针对调用者进行限流，填写微服务名，默认default(不区分来源)
阈值类型单机阈值:
QPS(每秒钟的请求数量)︰当调用该api的QPS达到阈值的时候，进行限流
线程数:当调用该api的线程数达到阈值的时候，进行限流
心
是否集群:不需要集群
流控模式:
直接: api达到限流条件时，直接限流
关联:当关联的资源达到阈值时，就限流自己
链路:只记录指定链路上的流量(指定资源从入口资源进来的流量，如果达到阈值，就进行限流)【api级
别的针对来源】
流控效果:
快速失败:直接失败，抛异常
 Warm Up:根据codeFactor(冷加载因子，默认3)的值，从阈值/codeFactor，经过预热时长，才达到设
置的QPS阈值
排队等待:匀速排队，让请求以匀速的速度通过，阈值类型必须设置为QPS，否则无效

## 13、SpringCloud Alibaba Seata处理分布式事务


