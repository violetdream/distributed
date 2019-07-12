package com.personal.rpc.registry.registrycenter;

/**
 * 2019/7/11/0011
 * Create by 刘仙伟
 */
public interface IRegistryCenter {

    /**
     *服务注册名称和服务注册地址实现服务的管理
     * @param serviceName
     * @param serviceAddress
     */
    public void registry(String serviceName,String serviceAddress);
}
