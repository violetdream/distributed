package com.personal;

import java.util.concurrent.CountDownLatch;

/**
 * 2019/5/20/0020
 * Create by 刘仙伟
 */
public class CountDownLatchDemo extends  Thread implements Runnable{
    static CountDownLatch countDownLatch=new CountDownLatch(1);
    public static void main(String[] args) throws InterruptedException {
//        new Thread(()->{
//            System.out.println("CountDownLatchDemo.instance initializer" +Thread.currentThread().getName()+"执行中");
//            countDownLatch.countDown();
//            System.out.println(""+Thread.currentThread().getName()+"执行完毕");
//        },"t1").start();
//        new Thread(()->{
//            System.out.println("CountDownLatchDemo.instance initializer" +Thread.currentThread().getName()+"执行中");
//            countDownLatch.countDown();
//            System.out.println(""+Thread.currentThread().getName()+"执行完毕");
//        },"t2").start();
//        new Thread(()->{
//            System.out.println("CountDownLatchDemo.instance initializer" +Thread.currentThread().getName()+"执行中");
//            countDownLatch.countDown();
//            System.out.println(""+Thread.currentThread().getName()+"执行完毕");
//        },"t3").start();
//
//        countDownLatch.await();
        for (int i = 0; i < 1000; i++) {
            new CountDownLatchDemo().start();
        }
        countDownLatch.countDown();
        System.out.println("CountDownLatchDemo.main"+" All Stop!");
    }


    @Override
    public void run() {
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(""+Thread.currentThread().getName());
    }
}
