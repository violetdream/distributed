# 多线程基础
第一课 认识多线程，用多线程做数据迁移业务
第二课 多线程基础及原理
何为线程安全？
管理数据状态的访问，如何使用数据

# synchronized 重量级锁

修饰实例方法；修饰静态方法；修饰代码块；

修饰实例方法及修饰代码块synchronized(this)都是锁的当前实例；

synchronized(this)对象锁与synchronized(syncDemo.class)类锁

对象在内存中是如何存储的？

synchronized(lock)   -》对象头，实例数据，填充

对象头-A> age，锁标记，偏向锁标记 。。。

假如有两个锁线程ThreadA/ThreadB
1、只有ThreadA去访问（大部分情况是属于这种） ->引入了偏向锁
ThreadA的ThreadID，偏向锁标记1
2、ThreadA和ThreadB的交替访问  轻量级锁-》自旋
3、多线程同时访问  -》阻塞


什么是自旋
cas
for(;;){
 if(cas)
	return;//表示获得锁成功
}
绝大部分的线程在获得锁以后，在非常短的时间内会去释放锁

自旋会占用CPU资源，所以在指定的自旋次数之后，如果还没有获得轻量级锁，锁膨胀成重量级锁-》阻塞

设置自旋次数 preBlockSpin

自适应自旋

偏向锁（一般是关闭）-》轻量级锁

无锁-》轻量级锁-》重量级锁  （基于JVM底层的实现）

# 线程的通信机制
 wait notify notifyall
 
wait：第一个是实现线程的阻塞、会释放当前的同步锁。
notify/notifyall 

wait会释放锁资源，并且释放CPU资源
sleep并不会。

# volatile

保证数据可见性

如何保证可见性的
 通过hsdis查看，多了一个Lock的汇编指令

可见性到底是什么
 （1）硬件层面
 （2）JMM层面
 
CPU、内存、I/O设备

绝大部分程序中，需要内存，IO
最大化的利用CPU资源
1、CPU增加高速缓存
2、引入线程、进程
3、指令优化-》重排序

CPU的高速缓存
1、高速缓存会带来缓存不一致问题
CPU层面的解决方案
1、总线锁
2、缓存锁

缓存一致性协议（MESI）
什么是MESI？
mesi表达的是缓存行的四种状态 （CPU硬件层面的实现）

基于MESI协议可以解决缓存一致性问题？
不，MESI带来的问题：

storebuffer
value=3;
void cpu0(){
	value=10;（S->I状态） =》【storebuffer -> 通知其他CPU缓存执行失效
	
	isFinish=true;（E状态）
}
void cpu1(){
	if(isFInish){
	assert value==10;
	}
}

CPU的乱序执行-》重排序-》可见性问题

CPU层面提供了指令-》内存屏障

内存屏障用来解决可见性问题

CPU层面提供了三种屏障
写屏障、读屏障、全屏障

store barrier
load barrier
full barrier

volatile ->lock（缓存锁）->内存屏障 ->可见性

内存屏障、重排序。-》和平台以及硬件有关系

## JMM的内存模型

可见性问题的根本原因：高速缓存、重排序

JMM最核心的价值是解决了有序性、可见性
语言级别抽象内存模型-》

volatile synchronized final happens-before

原代码-》编译器的重排序-》CPU层面的重排序（指令级、内存）-》最终执行的指令

不是所有的程序都会进行重排序
数据依赖规则
int a=1;int b=a;
a=1;a=2;
b=0;a=b;b=1;

不管你怎么重排序，对于单个线程的执行结果不能变

(1)int a=2;
(2)int b=3;
(3)int rs=a*b;
1 2 3  2 1 3


JMM->内存屏障  
编译器级别（语言级别的内存屏障）/CPU层面（内存屏障）

load1 loadload load2 ->load1 早于load2 执行
load loadstore store -> load 早于store 执行


## Happens-Before规则
可见性的保障->volatile以外，还提供了其它方法

A happens-before B

哪些操作会建立happens-before原则
1、程序的顺序规则
2、volatile规则
3、传递性规则
1 happens-before 2 3 happens-before 4-> 1 happens-before 4
4、start规则
5、join规则
	相当于阻塞了主线程，获取到线程的执行结果
6、synchronized 监视器锁规则

volatile不解决原子性问题，禁止指令重排序可以解决可见性

synchronized可以解决原子性、有序性，可见性


其实还不明白 最后加了内存屏障和为解决CPU高速缓存阻塞的问题不是冲突了吗

内存屏障与引入StoreBuffer之前的阻塞有什么区别

内存屏障解决了重排序使可见性，不影响效率？缓存锁会影响效率？


负载均衡算法
1、轮询
2、加权轮询
3、随机算法
4、最小连接数
5、hash  根据客户端ip计算hash值，hash%2

Session+cookie
session服务端保存状态
cookie 客户端保存状态

## session共享
1、session复制
2、session共享，统一存储（如大部分在用的redis)
3、无状态存储，生成一个标识，access_token。

## 数据库性能提升
1、读写分离->读多写少的场景
2、分库分表->历史库

数据如何同步 mysql-master / slave
## 应用如何对数据源进行路由  
mycat / sharding-jdbc

特性的问题
搜索引擎：lucence、solr、elasticsearch(商业服务)

索引 -> 
全量构建 ->第一次构建
增量 ->增量同步
非实时（异步/定时任务)、实时
ElasticSearch

## NoSql存储或者其他存储方式 
hbase、mongoDB、Redis

浏览器缓存、应用缓存、数据库缓存、CPU缓存

文件存储  oss、hdfs

## 分布式数据库
通过分表分库实现分布式数据库（用户为，商品库，交易库）

1、分布式事务问题 ->分布式数据一致性问题

Spring Cloud alibaba ->fescar (seata) /RocketMQ ->分布式事务解决方案

## 服务调用

高可用方案
1、单点故障
多机房部署
基于当前用户所在的位置去找到最近一个机房进行数据的传输

## 监控
1、链路监控 zipkin  traceID
2、硬件监控 CPU 内存 磁盘

全链路压力测试、单节点压力测试  loadrunner、jemeter、apache ab工具

wireshark抓包工具

##影响RPC通信性能的因素
1、序列化的计算性能
2、数据包的大小
3、跨语言
webservice xml格式

##序列化
XML
方便阅读，
JSON
http通信->json
jakson/fastjson/Gson/
hessian二进制序列协议
Avro: -apache->avro序列化
kyro: dubbo、hive/storm
protobuf: google

protobuf序列化

protostuff

什么是序列化，Java是如何实现序列化的
>> 序列化是把对象的状态信息转化为可存储或传输的形式过程，也就是把对象转化为字节序列的过程称为对象的序列化；
反序列化是序列化的逆向过程，把字节数组反序列化为对象，把字节序列恢复为对象的过程称为对象的反序列化。
Java通过java.io.ObjectOutputStream和对象输入流java.io.ObjectInputStream来实现。

如果一个子类实现了序列化，父类没有实现，那么父类中的成员变量能否被序列化？
>> 父类成员变量也能被序列化

你有了解过哪些序列化技术？以及他们之间的差异性？
>> XML序列化；可读性好，方便阅读及调试；但序列化后的字节码文件较大，效率不高
JSON序列化；是一种轻量级的数据交换格式，字节流相对较小，可以读性也好，使用较为普遍
Hessian序列化；一个支持跨语言传输的二进制序列化协议
Avro序列化；支持二进行序列化，可以便捷，快速地处理大量数据；
Kyro序列化；不能跨语言，一种成熟的序列化实现
protobuf:Google的一种数据交换格式，它独立于语言，独立于平台 ，有相应语言的编译器和库文件，空间开销小和性能比较好；使用较为麻烦，要投入成本去学习

transient是干嘛的？
>> 控制变量的序列化，可以阻止该变量被序列化到文件中
有什么方法能够绕过transient的机制。这个实现机制的原理是什么？
>> 通过定义writeObject和readObject两个私有方法；能过反射调用赋值变量
serializable的安全性如何保证？7.有没有了解过protobuf，它的序列化实现原理是什么？
>> 没有了解过，protobuf通过约定顺序加压缩字节码实现
serialVersionUID的 作 用 是 什 么 ？ 如 果 我 不 设 置serialVersionUID,有没有问题？
>> java序列化机制是通过判断类的serialVersionUID来验证版本一致性的，在进行反序列化时，JVM会把传过来的字节流中的serialVersionUID与本地相应实体类的serialVersionUID进行比较。
如果不设置，Java编译器会自动给这个class进行一个摘要算法 ，生成一个serialVersionUID。