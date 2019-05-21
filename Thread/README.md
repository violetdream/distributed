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

##Conditions
Synchronized wait/notify/notifyAll
condition  await/signal

生产者、消费者
wait/notify;   condition:await/signal

##CountDownLatch
使用场景：计数器
await/countdown

CountDownLatch会用到AQS的哪种锁？共享锁

里面有getState()，state表示计数器，

共享锁不需要竞争

##Semaphore
限流（AQS）
permits 令牌（5）
公平和非公平 
semaphore.acquire()
semaphore.release();

state表示令牌数

##CycliBarrier
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