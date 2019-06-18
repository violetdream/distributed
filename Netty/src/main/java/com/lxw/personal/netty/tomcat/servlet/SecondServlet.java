package com.lxw.personal.netty.tomcat.servlet;

import com.lxw.personal.netty.tomcat.http.LXWRequest;
import com.lxw.personal.netty.tomcat.http.LXWResponse;
import com.lxw.personal.netty.tomcat.http.LXWServlet;

public class SecondServlet extends LXWServlet {

	public void doGet(LXWRequest request, LXWResponse response) throws Exception {
		this.doPost(request, response);
	}

	public void doPost(LXWRequest request, LXWResponse response) throws Exception {
		response.write("This is Second Serlvet");
	}

}
