package com.lxw.personal.netty.tomcat.http;

/**
 * 2019/6/18/0018
 * Create by 刘仙伟
 */
public abstract class LXWServlet {

    public void service(LXWRequest request,LXWResponse response)throws Exception{
        if("GET".equalsIgnoreCase(request.getMethod())){
            doGet(request,response);
        }else {
            doPost(request,response);
        }
    }
    public abstract void doGet(LXWRequest request,LXWResponse response)throws Exception;
    public abstract void doPost(LXWRequest request,LXWResponse response)throws Exception;

}
