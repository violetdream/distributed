package com.personal;

import com.sun.jmx.remote.internal.ArrayQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 2019/5/20/0020
 * Create by 刘仙伟
 */

public class ParkQueue {
    private final int QUEUE_SIZE=10;
    private ArrayQueue queue= new ArrayQueue(QUEUE_SIZE);
    private ReentrantLock lock=new ReentrantLock();
    private Condition takeCondition=lock.newCondition();
    private Condition putCondition=lock.newCondition();
    private int i=0;

    public void put(){
        lock.lock();
        try {
            if(QUEUE_SIZE==queue.size()){
                putCondition.await();
            }
            System.out.println("before add queue.size() = " + queue.size());
            queue.add(++i);
            System.out.println("i = " + i);
            takeCondition.signal();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
        }

    }
    public void take(){
        lock.lock();
        try{
            if(queue.isEmpty()){
                takeCondition.await();
            }
            System.out.println("before remove queue.size() = " + queue.size());
            System.out.println(" "+queue.remove(0));
            putCondition.signal();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
        }



    }
}
