package com.lxw.personal;

import com.lxw.personal.zkclient.ZKClientProcessMutex;
import org.I0Itec.zkclient.ZkClient;

import java.util.concurrent.TimeUnit;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        ZkClient zkClient=new ZkClient(ZKConfig.CONNECTION_STR,30000,10000);
        ZKClientProcessMutex lock=new ZKClientProcessMutex(zkClient,"/locks");
        for(int i=0;i<10;i++){
            new Thread(()->{
                System.out.println(Thread.currentThread().getName()+"->尝试获取锁");
                try{
                    lock.acquire();
                    System.out.println(Thread.currentThread().getName()+"->获得锁成功");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    TimeUnit.SECONDS.sleep(4);
                    lock.release();
                    System.out.println(Thread.currentThread().getName()+"->释放锁成功");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            },"t"+i).start();
        }
    }
}
