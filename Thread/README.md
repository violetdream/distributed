# 学习并认识多线程
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
程序一的执行结果如下：
x=1000
Main x=2000

Main x=2000
x=1000

Main x=1000
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

程序二的执行结果取决于在主线程执行打印时，1000个线程的执行数；因为锁的是count对象，count++后是重新建一个Integer类型去赋值，锁的不是同一个对象无意义；
