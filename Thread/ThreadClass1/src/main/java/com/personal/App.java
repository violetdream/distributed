package com.personal;

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
    }
}
