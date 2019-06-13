package com.lxw.personal;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * 2019/6/12/0012
 * Create by 刘仙伟
 */
public class NIOServer {
    private Selector selector;

    private ByteBuffer buffer=ByteBuffer.allocate(1024);

    private int port;

    public NIOServer(int port){
        try {
            ServerSocketChannel server=ServerSocketChannel.open();
            this.port=port;
            server.bind(new InetSocketAddress(port));

            //BIO升级NIO  为了兼容BIO，NIO默认是阻塞模式
            server.configureBlocking(false);

            //准备就绪，接客
            selector=Selector.open();

            //在门口翻牌子，正在营业
            server.register(selector, SelectionKey.OP_ACCEPT);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listen(){
        //轮询主线程
        try {
            while(true){
                //大堂在叫号
                selector.select();
                //每次都拿 到所有的号
                Set<SelectionKey> keys=selector.selectedKeys();
                //不断地迭代，

                Iterator<SelectionKey> iter=keys.iterator();
                //同步体现在这里，因为每次只能拿一个Key，只能处理一种状态
                while(iter.hasNext()){
                    SelectionKey key=iter.next();
                    iter.remove();
                    //每一个key代表一种状态
                    //每一个号对应一个业务
                    //数据就绪，数据可读，数据可写等等
                    process(key);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //在同一个时间点，只能干一件事
    private void process(SelectionKey key) throws IOException {
        if(key.isAcceptable()){
            ServerSocketChannel server= (ServerSocketChannel) key.channel();
           //这个方法体现非阻塞，不管数据有没有准备好
            //你给我一个状态和反馈
            SocketChannel channel=server.accept();
            channel.configureBlocking(false);
            //将数据准备就绪的时候，将状态改为可读
            key=channel.register(selector,SelectionKey.OP_READ);
        }else if(key.isReadable()){
            SocketChannel channel= (SocketChannel) key.channel();
            int len=channel.read(buffer);
            if(len>0){
               buffer.flip();
               String content=new String(buffer.array(),0,len);
                key=channel.register(selector,SelectionKey.OP_WRITE);
                //在key上携带一个附件，一会再写出去
                key.attach(content);
                System.out.println("content = " + content);

            }
        }else if(key.isWritable()){
            SocketChannel channel= (SocketChannel) key.channel();
            String content= (String) key.attachment();
            channel.write(ByteBuffer.wrap(("输出:"+content).getBytes()));
        }
    }

    public static void main(String[] args) {
        NIOServer nioServer=new NIOServer(8080);
        nioServer.listen();
    }
}
