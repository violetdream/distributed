package com.lxw.personal.netty.tomcat.servlet;

import com.lxw.personal.netty.tomcat.http.LXWRequest;
import com.lxw.personal.netty.tomcat.http.LXWResponse;
import com.lxw.personal.netty.tomcat.http.LXWServlet;

/**
 * 2019/6/18/0018
 * Create by 刘仙伟
 */
public class ThirdServlet extends LXWServlet {
    @Override
    public void doGet(LXWRequest request, LXWResponse response) throws Exception {
        doPost(request,response);
    }

    @Override
    public void doPost(LXWRequest request, LXWResponse response) throws Exception {
        response.write("This is Third Serlvet");
    }
}
