package com.lxw.personal;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 2019/6/12/0012
 * Create by 刘仙伟
 */
public class BIOServer {
    private ServerSocket server;
    public BIOServer(int port){
        try {
            server=new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listen(){
        while(true){
            try {
                Socket socket=server.accept();
                InputStream inputStream=socket.getInputStream();
                byte[] buffer=new byte[1024];
                int length=inputStream.read(buffer);
                if(length>0){
                    String readString=new String(buffer);
                    System.out.println("readString = " + readString);;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        BIOServer bioServer=new BIOServer(8080);
        bioServer.listen();
    }
}
