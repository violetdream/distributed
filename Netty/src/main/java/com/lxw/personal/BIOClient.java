package com.lxw.personal;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * 2019/6/12/0012
 * Create by 刘仙伟
 */
public class BIOClient {

    public void send(){
        try {
            Socket socket=new Socket("127.0.0.1",8080);
            OutputStream outputStream=socket.getOutputStream();
            byte[] bytes="你想做什么，随你。。。。".getBytes();
            outputStream.write(bytes);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        BIOClient bioClient=new BIOClient();
        bioClient.send();
    }
}
