package com.personal.rpc.provider;

import com.personal.rpc.api.IRPCHelloService;

/**
 * 2019/6/17/0017
 * Create by 刘仙伟
 */
public class RPCHelloServiceImpl implements IRPCHelloService {

    public String hello(String name) {
        return "Hello " + name + "!";
    }
}
