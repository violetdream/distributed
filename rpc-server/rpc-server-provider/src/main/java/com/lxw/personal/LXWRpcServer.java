package com.lxw.personal;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 2019/6/11/0011
 * Create by 刘仙伟
 */
public class LXWRpcServer implements ApplicationContextAware,InitializingBean {
    private static LXWRpcServer ourInstance = new LXWRpcServer(8080);

    public static LXWRpcServer getInstance() {
        return ourInstance;
    }
    ExecutorService executorService= Executors.newCachedThreadPool();
    private Map<String,Object> handlerMap=new HashMap();
    private  int port;
    public LXWRpcServer(int port) {
        this.port=port;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ServerSocket serverSocket=null;
        try{
            serverSocket=new ServerSocket(port);
            while(true){
                Socket socket=serverSocket.accept();
                executorService.execute(new ProcessHandler(socket,handlerMap));
            }
        }catch(IOException e){
            e.printStackTrace();
        }finally {
            if(serverSocket!=null){
                try{
                    serverSocket.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String,Object> serviceBeanMap=applicationContext.getBeansWithAnnotation(RpcService.class);
        if(!serviceBeanMap.isEmpty()){
            for(Object serviceBean:serviceBeanMap.values()){
                RpcService rpcService=serviceBean.getClass().getAnnotation(RpcService.class);
                String serviceName=rpcService.value().getName();
                String version=rpcService.version();
                if(!StringUtils.isEmpty(version)){
                    serviceName+="-"+version;
                }
                handlerMap.put(serviceName,serviceBean);
            }
        }
    }
}
