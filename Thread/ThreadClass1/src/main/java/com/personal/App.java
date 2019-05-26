package com.personal;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Hello world!
 *
 */
public class App 
{
    static ParkQueue parkQueue=new ParkQueue();

    static class PutDemo extends Thread{
        @Override
        public void run() {
            parkQueue.put();
        }
    }
    static class TakeDemo extends Thread{
        @Override
        public void run() {
            parkQueue.take();
        }
    }
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );

        for (int i = 0; i < 10; i++) {
            new PutDemo().start();
        }
        for (int i = 0; i < 10; i++) {
            new TakeDemo().start();
        }

        ExecutorService executorService= Executors.newFixedThreadPool(10);
        executorService=Executors.newCachedThreadPool();
        executorService=Executors.newSingleThreadExecutor();
        executorService=Executors.newScheduledThreadPool(1);
//        executorService=Executors.newWorkStealingPool();

        executorService.submit(new Runnable() {
            @Override
            public void run() {

            }
        });
        ThreadPoolExecutor threadPoolExecutor=(ThreadPoolExecutor)executorService;
        threadPoolExecutor.prestartAllCoreThreads();


        threadPoolExecutor.shutdown();
        threadPoolExecutor.shutdownNow();
        System.out.println("threadPoolExecutor = " + ((1 << (Integer.SIZE - 3)) - 1));
        System.out.println("App.main"+Integer.MAX_VALUE);
    }
}
