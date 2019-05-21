package com.personal;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * 2019/5/20/0020
 * Create by 刘仙伟
 */
public class SemaphoreDemo extends Thread{

    static Semaphore semaphoreDemo=new Semaphore(5);

    @Override
    public void run() {
        try {
            semaphoreDemo.acquire();
            System.out.println("semaphoreDemo+Thread.currentThread().getName() = " + semaphoreDemo+Thread.currentThread().getName());
            TimeUnit.SECONDS.sleep(2);
            System.out.println(""+Thread.currentThread().getName());
            semaphoreDemo.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        for (int i = 0; i <10 ; i++) {
            new SemaphoreDemo().start();
        }
    }
}
