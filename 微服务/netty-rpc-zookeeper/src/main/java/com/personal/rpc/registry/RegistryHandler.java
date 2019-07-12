package com.personal.rpc.registry;

import com.personal.rpc.protocol.InvokerProtocol;
import com.personal.rpc.registry.registrycenter.RegistryCenterwithZK;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 2019/6/17/0017
 * Create by 刘仙伟
 */
public class RegistryHandler extends ChannelInboundHandlerAdapter {
    //用保存所有可用的服务
    public static ConcurrentHashMap<String, Object> registryMap = new ConcurrentHashMap<String,Object>();

    private RegistryCenterwithZK registryCenterwithZK=new RegistryCenterwithZK();

    private int port;

    //保存所有相关的服务类
    private List<String> classNames = new ArrayList<String>();
    public RegistryHandler(int port) {
        this.port=port;
        //完成递归扫描
        scannerClass("com.personal.rpc.provider");
        doRegister();
    }
    /*
     * 递归扫描
     */
    private void scannerClass(String packageName) {
        URL url = this.getClass().getClassLoader().getResource(packageName.replaceAll("\\.", "/"));
       try {
           File dir = new File(URLDecoder.decode(url.getFile(),"UTF-8"));
           for (File file : dir.listFiles()) {
               //如果是一个文件夹，继续递归
               if(file.isDirectory()){
                   scannerClass(packageName + "." + file.getName());
               }else{
                   classNames.add(packageName + "." + file.getName().replace(".class", "").trim());
               }
           }
       }catch (Exception e){
           e.printStackTrace();
       }


    }

    /**
     * 完成注册
     */
    private void doRegister(){
        if(classNames.size() == 0){ return; }
        for (String className : classNames) {
            try {
                Class<?> clazz = Class.forName(className);
                Class<?> i = clazz.getInterfaces()[0];
                registryMap.put(i.getName(), clazz.newInstance());
                registryCenterwithZK.registry(i.getName(),getAddress()+":"+port);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String getAddress() {
        InetAddress inetAddress = null;
        try {
            inetAddress=InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return inetAddress.getHostAddress();
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Object result=new Object();
        InvokerProtocol request= (InvokerProtocol) msg;
        if(registryMap.containsKey(request.getClassName())){
            Object clazz = registryMap.get(request.getClassName());
            Method method = clazz.getClass().getMethod(request.getMethodName(), request.getParames());
            result = method.invoke(clazz, request.getValues());
        }
        ctx.write(result);
        ctx.flush();
        ctx.close();
    }
}
