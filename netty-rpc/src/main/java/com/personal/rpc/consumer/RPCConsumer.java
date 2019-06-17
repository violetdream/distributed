package com.personal.rpc.consumer;

import com.personal.rpc.api.IRPCHelloService;
import com.personal.rpc.api.IRPCService;

/**
 * 2019/6/16/0016
 * Create by 刘仙伟
 */
public class RPCConsumer {
    public static void main(String[] args) {
        IRPCHelloService rpcHello = RPCProxy.create(IRPCHelloService.class);

        System.out.println(rpcHello.hello("Tom老师"));

        IRPCService service = RPCProxy.create(IRPCService.class);


        System.out.println("8 + 2 = " + service.add(8, 2));
        System.out.println("8 - 2 = " + service.sub(8, 2));
        System.out.println("8 * 2 = " + service.mult(8, 2));
        System.out.println("8 / 2 = " + service.div(8, 2));
    }
}
