package com.lxw.personal;

import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Map;

/**
 * 2019/6/11/0011
 * Create by 刘仙伟
 */
public class ProcessHandler implements Runnable{

    private Socket socket;
    private Map<String,Object> handlerMap;

    public ProcessHandler(Socket socket,Map<String,Object> handlerMap){
        this.socket=socket;
        this.handlerMap=handlerMap;
    }

    public void run(){
        ObjectInputStream objectInputStream=null;
        ObjectOutputStream objectOutputStream=null;
        try {
            objectInputStream=new ObjectInputStream((socket.getInputStream()));
            RpcRequest rpcRequest= (RpcRequest) objectInputStream.readObject();
            Object result=invoke(rpcRequest); //反射调用本地服务

            objectOutputStream=new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(result);
            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    private Object invoke(RpcRequest request) throws NoSuchMethodException, ClassNotFoundException, InvocationTargetException, IllegalAccessException {
        String serviceName=request.getClassName();
        String version=request.getVersion();
        //增加版本号的判断
        if(!StringUtils.isEmpty(version)){
            serviceName+="-"+version;
        }

        Object service=handlerMap.get(serviceName);
        if(service==null){

            throw new RuntimeException("service not found:"+serviceName);
        }

        Object[] args=request.getParameters(); //拿到客户端请求的参数
        Method method=null;
        if(args!=null) {
            Class<?>[] types = new Class[args.length]; //获得每个参数的类型
            for (int i = 0; i < args.length; i++) {
                types[i] = args[i].getClass();
            }
            Class clazz=Class.forName(request.getClassName()); //跟去请求的类进行加载
            method=clazz.getMethod(request.getMethodName(),types); //sayHello, saveUser找到这个类中的方法
        }else{
            Class clazz=Class.forName(request.getClassName()); //跟去请求的类进行加载
            method=clazz.getMethod(request.getMethodName()); //sayHello, saveUser找到这个类中的方法
        }

        Object result=method.invoke(service,args);//HelloServiceImpl 进行反射调用
        return result;

    }
}
