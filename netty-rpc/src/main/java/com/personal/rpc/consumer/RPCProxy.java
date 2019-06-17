package com.personal.rpc.consumer;

import com.personal.rpc.protocol.InvokerProtocol;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 2019/6/17/0017
 * Create by 刘仙伟
 */
public class RPCProxy {
    public static <T> T create(Class<?> clazz){
        MethodProxy methodProxy=new MethodProxy(clazz);
        Class<?>[] interfaces=clazz.isInterface()?new Class[]{clazz}:clazz.getInterfaces();
        T result= (T) Proxy.newProxyInstance(clazz.getClassLoader(),interfaces,methodProxy);
        return result;
    }

    private static class MethodProxy implements InvocationHandler{
        private Class<?> clazz;
        public MethodProxy(Class<?> clazz){
            this.clazz=clazz;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            final RPCProxyHandler handler=new RPCProxyHandler();
            if(Object.class.equals(method.getDeclaringClass())){
                return method.invoke(proxy,args);
            }else{
                //传输协议封装
                InvokerProtocol msg=new InvokerProtocol();
                msg.setClassName(this.clazz.getName());
                msg.setMethodName(method.getName());
                msg.setValues(args);
                msg.setParames(method.getParameterTypes());


                EventLoopGroup eventLoopGroup=new NioEventLoopGroup();
                try{
                    Bootstrap b=new Bootstrap();
                    b.group(eventLoopGroup).channel(NioSocketChannel.class)
                            .option(ChannelOption.TCP_NODELAY,true)
                            .handler(new ChannelInitializer<SocketChannel>() {
                                @Override
                                protected void initChannel(SocketChannel socketChannel) throws Exception {
                                    ChannelPipeline pipeline = socketChannel.pipeline();
                                    pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                                    pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
                                    pipeline.addLast("encoder", new ObjectEncoder());
                                    pipeline.addLast("decoder",new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
                                    pipeline.addLast("handler",handler);
                                }
                            });
                    ChannelFuture future = b.connect("localhost", 8080).sync();
                    future.channel().writeAndFlush(msg).sync();
                    future.channel().closeFuture().sync();
                }catch (Exception e){
                    throw e;
                }finally {
                    eventLoopGroup.shutdownGracefully();
                }
            }
            return handler.getResponse();
        }
    }
}
