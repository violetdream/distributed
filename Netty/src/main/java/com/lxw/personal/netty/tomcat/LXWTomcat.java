package com.lxw.personal.netty.tomcat;

import com.lxw.personal.netty.tomcat.http.LXWRequest;
import com.lxw.personal.netty.tomcat.http.LXWResponse;
import com.lxw.personal.netty.tomcat.http.LXWServlet;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 2019/6/18/0018
 * Create by 刘仙伟
 */
public class LXWTomcat {
    private int port = 8080;
    private Map<String, LXWServlet> servletMap = new HashMap<String, LXWServlet>();
    private Properties web = new Properties();

    private void init() {
        try {
            String WEB_INF = URLDecoder.decode(this.getClass().getResource("/").getPath());
            FileInputStream fis = new FileInputStream(WEB_INF + "web.properties");
            web.load(fis);
            for (Object k : web.keySet()) {
                String key = k.toString();
                if (key.endsWith(".url")) {
                    String servletName = key.replaceAll("\\.url$", "");
                    String url = web.getProperty(key);
                    String className = web.getProperty(servletName + ".className");
                    LXWServlet servlet = (LXWServlet) Class.forName(className).newInstance();
                    servletMap.put(url, servlet);
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public void start(){
        init();
        EventLoopGroup eventLoopGroup=new NioEventLoopGroup();
        EventLoopGroup workerLoopGroup=new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap=new ServerBootstrap();
            serverBootstrap.group(eventLoopGroup,workerLoopGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        protected void initChannel(SocketChannel client) throws Exception {
                            client.pipeline().addLast(new HttpResponseEncoder());
                            client.pipeline().addLast(new HttpRequestDecoder());
                            client.pipeline().addLast(new TomcatHandler());
                        }
                    }).option(ChannelOption.SO_BACKLOG,128)
                    .childOption(ChannelOption.SO_KEEPALIVE,true);
            ChannelFuture future=serverBootstrap.bind(port).sync();
            System.out.println("Tomcat 已启动，监听的端口是： " + port);
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            eventLoopGroup.shutdownGracefully();
            workerLoopGroup.shutdownGracefully();
        }

    }
    public class TomcatHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (msg instanceof HttpRequest){
                HttpRequest req = (HttpRequest) msg;

                // 转交给我们自己的request实现
                LXWRequest request = new LXWRequest(ctx,req);
                // 转交给我们自己的response实现
                LXWResponse response = new LXWResponse(ctx,req);
                // 实际业务处理
                String url = request.getUrl();

                if(servletMap.containsKey(url)){
                    servletMap.get(url).service(request, response);
                }else{
                    response.write("404 - Not Found");
                }

            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        }

    }

    public static void main(String[] args) {
        new LXWTomcat().start();
    }
}

