## 微服务架构中需要哪些工具
>> Spring Cloud是一种标准；Spring万能的粘合剂；Spring中大部分的组件都是做整合；
http RESTFUL
Spring Cloud zuul/Spring Cloud GateWay  监权，日志，限流，灰度
Spring Cloud Eureka/consul/zookeeper 
Spring Cloud Ribbon
Spring Cloud Hystrix/Sentinel
Spring Cloud Config+bus diamond/

Spring Boot
AutoConfiguration
Starter
Actuator
SpringBoot CLI

Alibaba		NetFlix Oss

Dubbo		Eureka
RocketMQ	Feign
Nacos		Ribbon
Sentinel	Hystrix
Seta 

1. soa和微服务的区别
>> 微服务并不是一种新思想的方法，它更像是一种思想的精练，是一种服务化思想的最佳实践方向而已，微服务其实是在SOA思路下，
随着各个企业对于服务化治理上不断的完善。微服务也是一种面向服务的架构模型，只是它更强调服务的粒度。也就是服务的职责
更加单一更加精练，我们可以把SOA看成是微服务的超集。也就是多个微服务可以组成一个SOA服务。
1、SOA关注的是服务的重用性，以及解决企业内部信息孤岛问题；
2、微服务关注的是解耦，解耦和可重用性在特定的角度来看是一样，但本质上是不同的。解耦是降低业务之间的耦合度（也就是微服务关注的服务粒度），
而可重用性关注的是服务的复用；
3、微服务会使用更轻量级的通信协议，使用Restful风格的API，轻量级协议可以很好的支持跨语言，使得语言生态更加丰富；
4、微服务会更多的关注Devops的持续交付，因为服务粒度更细使得开发运维变得更加重要，所以微服务对于容器化技术的结合更加紧密。
5、SOA应该是微服务的超集

2. 你是怎么理解微服务的？
>> 微服务强调的是服务的一种粒度，将整个应用系统进行解耦，使之变成各个子系统，引入服务化思想，通过实现各个服务之间的一种远程调用，使服务可以部署在
各个应用服务器上，达到系统在硬件上的分离，从而满足更多用户量的并发访问。

3. 什么是SpringCloud
>> SpringCloud是实现微服务架构的一套标准。Spring Cloud提供了一些可以让开发者快速构建分布式应用的工具。


4. 微服务架构的优点和缺点有哪些？
>> 优点：实现了服务调用者与服务提供者之间的解耦
	缺点：带给业务人员的一系列问题，业务人员在做微服务开发时需要处理一系列比较基础的事情，比如服务注册、服务发现、负载均衡、服务熔断和重试等。
	这些功能对于每一个业务程度员来说，都必须要了解和掌握，而实际上这些和业务功能并没有太大的关系，它理应是一个基础组件。

5. SpringCloud解决了什么问题？
>> Spring Cloud提供了一些可以让开发者快速构建分布式应用的工具。它整合了开源的基础组件，使得开发人员可以使用很少的代码来实现这些服务治理的功能。