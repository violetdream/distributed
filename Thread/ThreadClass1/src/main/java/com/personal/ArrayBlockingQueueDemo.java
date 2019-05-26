package com.personal;

import java.util.concurrent.*;

/**
 * 2019/5/23/0023
 * Create by 刘仙伟
 */
public class ArrayBlockingQueueDemo {

    private static final ExecutorService singleExecutor= Executors.newSingleThreadExecutor();
    private static final ArrayBlockingQueue arrayBlockingQueue=new ArrayBlockingQueue(10);


    public static class TransferService{
        {
            singleExecutor.execute(()->{
                while(true){
                    try {
                        String content= (String) arrayBlockingQueue.take();
                        //发送短信；
                        sendSMS(content);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        public void transfer(){
            System.out.println("转账中。。。");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            arrayBlockingQueue.add("***先生，您好，您刚在***银行個人網銀尾號為2210的賬戶轉賬了5000.00元。");
        }
    }

    private static void sendSMS(String content) {
        System.out.println("发送短信。。。");
        System.out.println("content = " + content);

    }

    public static void main(String[] args) {

        TransferService transferService=new TransferService();
        transferService.transfer();
        transferService.transfer();
    }
}
