package com.personal.rpc.provider;

import com.personal.rpc.api.IRPCService;

/**
 * 2019/6/17/0017
 * Create by 刘仙伟
 */
public class RPCServiceImpl implements IRPCService {

    public int add(int a, int b) {
        return a+b;
    }

    public int sub(int a, int b) {
        return a-b;
    }

    public int mult(int a, int b) {
        return a*b;
    }

    public int div(int a, int b) {
        return a/b;
    }
}
