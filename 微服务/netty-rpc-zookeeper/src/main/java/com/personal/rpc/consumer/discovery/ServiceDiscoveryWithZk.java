package com.personal.rpc.consumer.discovery;

import com.personal.rpc.registry.registrycenter.ZKConfig;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.ArrayList;
import java.util.List;

/**
 * 2019/7/12/0012
 * Create by 刘仙伟
 */
public class ServiceDiscoveryWithZk implements IServiceDiscovery{
    private CuratorFramework curatorFramework=null;
    private List<String> serviceRepos=new ArrayList<>(); //服务地址的本地缓存
    public ServiceDiscoveryWithZk(){
        //初始化zookeeper的连接， 会话超时时间是5s，衰减重试
        curatorFramework= CuratorFrameworkFactory.builder()
                .connectString(ZKConfig.CONNECTION_STR).sessionTimeoutMs(5000)
                .retryPolicy(new ExponentialBackoffRetry(1000,3))
                .namespace("registry").build();
        curatorFramework.start();
    }

    @Override
    public String discovery(String serviceName) {
        //完成了服务地址的查找(服务地址被删除)
        String servicePath="/"+serviceName;
        if(serviceRepos.isEmpty()){
            try {
                serviceRepos=curatorFramework.getChildren().forPath(servicePath);
                System.out.println("初次获取 serviceRepos = " + serviceRepos);
                registryWatch(servicePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //针对已有的地址做负载均衡
        LoadBalanceStrategy loadBalanceStrategy=new RandomLoadBalance();
        return loadBalanceStrategy.selectHost(serviceRepos);
    }

    private void registryWatch(final String servicePath) throws Exception {
        PathChildrenCache nodeCache=new PathChildrenCache(curatorFramework,servicePath,true);
        PathChildrenCacheListener nodeCachaListener=(curatorFramework1,pathChildrenCacheEvent)->{
            System.out.println("客户端收到节点变更的事件");
            serviceRepos=curatorFramework1.getChildren().forPath(servicePath);
            System.out.println("节点变更 serviceRepos = " + serviceRepos);
        };
        nodeCache.getListenable().addListener(nodeCachaListener);
        nodeCache.start();
    }
}
