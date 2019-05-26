# 学习并认识多线程
``` java
public class SynchronizedDemo implements Runnable{
    int x = 100;

    public synchronized void m1() {
        x = 1000;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("x=" + x);
    }

    public synchronized void m2() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        x = 2000;
    }
	public static void main(String[] args) throws InterruptedException {
        SynchronizedDemo sd = new SynchronizedDemo();
        new Thread(()->sd.m1()).start();
        new Thread(()->sd.m2()).start();
        sd.m2();
        System.out.println("Main x=" + sd.x);
    }
    @Override
    public void run() {
        m1();
    }
}
```
程序一的执行结果如下：
* x=1000
Main x=2000
<br>

* Main x=2000
x=1000
<br>

* Main x=1000
x=1000


原因：
（1）打印顺序取决于线程谁先拿到锁，如主线程先拿到锁并执行完则结果为情况二
（2）x=1000是因为变量x,数据在线程内赋值，单线程内的执行是串行的，m1方法内的打印是对m1方法内赋值后的逻辑；主线程的打印是对m2方法后的逻辑；
上述方法均是锁的实例sd的方法m1()和m2()
其中有三个线程竞争锁，分别为main线程、线程Thread1、线程Thread2
其中main线程与线程Thread2竞争同一实例方法m2的锁；线程Thread1单独竞争实例方法m1的锁；
无论何时，当线程Thread1执行进入临界区是，JVM会把sd对象的对象头Mark Word的锁标志位设为"01"，同时用CAS操作把Thread1的线程ID记录到Mark Word中，此时进入偏向锁模式,x=1000
main线程与Thread2竞争同一实例方法m2的锁，如main先拿到进入代码块，Thread2通过CAS自旋竞争，一直上升到重量级锁，main仍未结束，等main执行完后，Thread2进入monitor enter；
(1)如thread2执行完后，main线程才打印Main x=2000; 如Thread2未执行完，main打印也是Main x=2000;
(2)如main刚释放完锁未打印，thread1刚执行完赋值，则Main x=1000

因进入m1的线程TIME_WAITTING时间比m2的明显较久，再加上m2的锁可上升到重量级锁，不可能出现，m1打印x=2000的情况；
<br>
``` java
public class SynchronizedDemo  {
   static Integer count=0;
   public static void incr(){
       synchronized (count) {
           try {
               Thread.sleep(1);
           } catch (InterruptedException e) {
               e.printStackTrace();
           }
           count++;
       }
   }
public static void main(String[] args) throws IOException, InterruptedException {
        for(int i=0;i<1000;i++){
            new Thread(()->SynchronizedDemo.incr()).start();
        }
        Thread.sleep(5000);
        System.out.println("result:"+count);
    }
}
```

程序二的执行结果取决于在主线程执行打印时，1000个线程的执行数；因为锁的是count对象，count++后是重新建一个Integer类型去赋值，锁的不是同一个对象无意义；

# 第二节课作业
1、请列出Happens-before的几种规则
<br>
>>程序顺序规则，volatiole变量规则，传递性规则，start规则，join规则，监视器锁规则

2、volatile 能使得一个非原子操作变成原子操作吗？为什么？
<br>
>>不能，volatile只能解决可见性及顺序一致性问题；

3、哪些场景适合使用Volatile
<br>
>>多线程间需要对共享变量具有可见性时。

4、如果对一个数组修饰volatile，是否能够保证数组元素的修改对其他线程的可见？为什么？
<br>
>>不能，数组是对成员变量或对象一个地址引用，volatile可保证对于对象数组的地址具有可见性，但是数组或对象内部的成员变量不具有可见性。

## Conditions
Synchronized wait/notify/notifyAll
condition  await/signal

生产者、消费者
wait/notify;   condition:await/signal

## CountDownLatch
使用场景：计数器
await/countdown

CountDownLatch会用到AQS的哪种锁？共享锁

里面有getState()，state表示计数器，

共享锁不需要竞争

## Semaphore
限流（AQS）
permits 令牌（5）
公平和非公平 
semaphore.acquire()
semaphore.release();

state表示令牌数

## CycliBarrier
循环屏障
可以使得一组线程达到一个同步点之前阻塞
cyclicBarrier.await()

## CurrentHashMap
ConcurrentHashMap1.8中是基于什么机制来保证线程安全性的
<br>
基于Node数组+CAS原子操作占位符+Synchronized对链表头结点加锁

ConcurrentHashMap通过get方法获取数据的时候，是否需要通过加锁来保证数据的可见性？为什么？
<br>
不需要，Node数组值通过Volatile的happens-before原则实现可见性

ConcurrentHashMap1.7和ConcurrentHashMap1.8有哪些区别？
<br>
从Segment锁机制到用Synchronized对Node锁，减小锁粒度
从数组+单向链表到数组+单向链表+红黑树结构

ConcurrentHashMap1.8为什么要引入红黑树？
<br>
单向链表的查找时间复杂度为O(N),红黑树的查找时间复杂度为O(logN)

## 线程池
JDK、Spring
<br>
Executors
``` java
		ExecutorService executorService= Executors.newFixedThreadPool(10);
        executorService=Executors.newCachedThreadPool();
        executorService=Executors.newSingleThreadExecutor();
        executorService=Executors.newScheduledThreadPool();
        executorService=Executors.newWorkStealingPool();
```
## ThreadPoolExecutor
new ThreadPoolExecutor(nThreads,核心线程数
							nThreads,最大线程数
						  0L,超时时间
						  TimeUnit.MILLISECONDS,超时时间的单位
						  new LinkedBlockingQueue<Runnable>()  阻塞队列
						  ThreadFactory threadFactory, 线程的工厂
                          RejectedExecutionHandler handler); 拒绝策略
						  
keepaliveTime怎么去监控线程进行回收？

<br>
1、不建议直接用Executors创建线程
<br>
2、线程池大小的设置；取决于硬件环境和软件环境
CPU的核心数；线程的执行情况（IO密集型（CPU时间片的切换）CPU核心数的２倍，CPU密集型（CPU利用率非常高，以CPU核心数为准 设置最大线程数为CPU核心数+1））
（线程等待的时间＋线程CPU时间）／线程CPU时间×CPU核心数
<br>

worker的独占不支持重入
1、lock 表示正在执行任务，不应该被中断
2、shutdown的时候，w.trylock()

<br>
拒绝策略
AbortPolicy
DiscardPolicy
DiscardOldestPolicy
CallerRunsPolicy

为什么要使用线程池？
通过用线程池解决在JVM里创建太多线程处理请求时，可能会使系统由于过度消耗内存或切换过度而导致系统资源不足的问题。
Executors提供的四种线程池:newSingleThreadExecutor,newFixedThreadPool,newCachedThreadPool,newScheduledThreadPool ，请说出他们的区别以及应用场景
newSingleThreadExecutor核心线程数及最大线程数都为1，适用于根据FIFO优先级任务执行的场景。
newFixedThreadPool核心线程数及最大线程数是指定值，适用于指定线程数的场景。
newCachedThreadPool创建一个可缓存线程池，空闲线程只有60s，超过后就会回收，适于处理请求时间较短的场景。 
newScheduledThreadPool创建一个可以指定线程数量的线程池，且带有延时和周期性执行任务的功能；

线程池有哪几种工作队列？
ArrayBlockingQueue 基于数组的先进先出队列
LinkedBlockingQueue基于链表的先进先出队列
SynchronousQueue 同步队列

线程池默认的拒绝策略有哪些
AbortPolicy 直接抛出异常
CallerRunsPolicy 
DiscardOldestPolicy 丢弃阻塞队列中靠前的任务
DiscardPolicy 直接丢弃任务

如何理解有界队列和无界队列
设定了固定大小的队列为有界队列；未设定固定大小的队列为无界队列；

线程池是如何实现线程回收的？ 以及核心线程能不能被回收？
当线程池中的核心线程数大于最大线程数时，会被回收；
工作线程回收条件有下面三个
a、参数allowCoreThreadTimeOut为true；
B、该线程在keepAliveTime时间内获取不到任务，即空闲这么长时间；
C、当前线程池大小 > 核心线程池大小corePoolSize。
核心线程也能被回收（allowCoreThreadTimeOut为true时）。
FutureTask是什么
FutureTask是Runnable和Future的结合，既可以执行任务又能保存结果。
Thread.sleep(0)的作用是什么
Thread.Sleep(0) 是你的线程暂时放弃cpu，也就是释放一些未用的时间片给其他线程或进程使用，就相当于一个让位动作

如果提交任务时，线程池队列已满，这时会发生什么
判断当前线程数是否小于最大线程数，如果小于则创建新的工作线程来执行该任务；如果当前线程数大于，则执行拒绝策略。

如果一个线程池中还有任务没有执行完成，这个时候是否允许被外部中断？
可以通过shutdownnow方法来中断线程的执行
