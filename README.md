# 多线程基础
第一课 认识多线程，用多线程做数据迁移业务
第二课 多线程基础及原理
何为线程安全？
管理数据状态的访问，如何使用数据

synchronized 重量级锁

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

