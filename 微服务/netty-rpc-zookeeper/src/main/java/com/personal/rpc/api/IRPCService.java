package com.personal.rpc.api;

/**
 * 2019/6/17/0017
 * Create by 刘仙伟
 */
public interface IRPCService {
    /** 加 */
    public int add(int a,int b);

    /** 减 */
    public int sub(int a,int b);

    /** 乘 */
    public int mult(int a,int b);

    /** 除 */
    public int div(int a,int b);
}
