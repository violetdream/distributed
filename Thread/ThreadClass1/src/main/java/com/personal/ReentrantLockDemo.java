package com.personal;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 2019/5/16/0016
 * Create by 刘仙伟
 */
public class ReentrantLockDemo {

    public static void main(String[] args) throws InterruptedException {
        Lock lock=new ReentrantLock();
        lock.lock();
        TimeUnit.MINUTES.sleep(1);
        lock.unlock();
    }
}
