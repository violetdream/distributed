package com.lxw.personal.curator;

import com.lxw.personal.ZKConfig;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkLock;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;

import java.util.concurrent.TimeUnit;

/**
 * 2019/7/15/0015
 * Create by 刘仙伟
 */
public class Demo {
    public static void main(String[] args) {
        CuratorFramework curatorFramework=null;
        InterProcessMutex lock=new InterProcessMutex(curatorFramework,"/locks");

        ZkClient zkClient=new ZkClient(ZKConfig.CONNECTION_STR);
        zkClient.countChildren("/registry");
        ZkLock zkLock=new ZkLock();
        zkClient.waitUntilExists("/lock", TimeUnit.MILLISECONDS,3);

    }
}
