package com.personal.rpc.registry.registrycenter;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**
 * 2019/7/11/0011
 * Create by 刘仙伟
 */
public class RegistryCenterwithZK implements IRegistryCenter{

    private CuratorFramework curatorFramework =null;

    public  RegistryCenterwithZK(){
        curatorFramework = CuratorFrameworkFactory.builder().connectString(ZKConfig.CONNECTION_STR)
                .sessionTimeoutMs(5000).retryPolicy(new ExponentialBackoffRetry(1000,3))
                .namespace("registry").build();
        curatorFramework.start();
    }

    @Override
    public void registry(String serviceName, String serviceAddress) {
        String servicePath="/"+serviceName;
        try {
            if(curatorFramework.checkExists().forPath(servicePath)==null){
                curatorFramework.create().creatingParentsIfNeeded()
                        .withMode(CreateMode.PERSISTENT).forPath(servicePath);
            }
            String addressPath=servicePath+"/"+serviceAddress;
            curatorFramework.create().withMode(CreateMode.EPHEMERAL).forPath(addressPath);
            System.out.println(addressPath+" 服务注册成功");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
