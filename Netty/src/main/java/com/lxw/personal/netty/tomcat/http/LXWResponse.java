package com.lxw.personal.netty.tomcat.http;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;

import java.io.UnsupportedEncodingException;

/**
 * 2019/6/18/0018
 * Create by 刘仙伟
 */
public class LXWResponse {
    private ChannelHandlerContext ctx;
    private HttpRequest req;

    public LXWResponse(ChannelHandlerContext ctx,HttpRequest req){
        this.ctx=ctx;
        this.req=req;
    }

    public void write(String out){
        try {
            if(out==null || out.length()==0){
                return;
            }
            FullHttpResponse response=new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(out.getBytes("UTF-8")));
            response.headers().set("Content-Type","text/html");
            ctx.write(response);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }finally {
            ctx.flush();
            ctx.close();
        }
    }
}
