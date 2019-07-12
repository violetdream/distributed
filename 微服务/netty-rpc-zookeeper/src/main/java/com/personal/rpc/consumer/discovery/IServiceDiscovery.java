package com.personal.rpc.consumer.discovery;

/**
 * 2019/7/12/0012
 * Create by 刘仙伟
 */
public interface IServiceDiscovery {
    //根据服务名称返回服务地址
    String  discovery(String serviceName);
}
