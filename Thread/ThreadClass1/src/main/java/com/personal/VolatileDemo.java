package com.personal;

/**
 * 2019/5/14/0014
 * Create by 刘仙伟
 */
public class VolatileDemo {
//    public volatile static boolean stop=false;
    public  static boolean stop=false;

    public static void main(String[] args) throws InterruptedException {
        Thread thread=new Thread(()->{
            int i=0;
            while(!stop){
                i++;
                System.out.println("i = " + i);
            }
        });
        thread.start();
        System.out.println("thread = " + thread);
        Thread.sleep(1000);
        stop=true;
    }
}
