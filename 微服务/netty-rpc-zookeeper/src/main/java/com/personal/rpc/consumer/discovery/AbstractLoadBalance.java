package com.personal.rpc.consumer.discovery;

import java.util.List;

/**
 * 2019/7/12/0012
 * Create by 刘仙伟
 */
public abstract class AbstractLoadBalance implements LoadBalanceStrategy{

    @Override
    public String selectHost(List<String> repos) {
        //repos可能为空， 可能只有一个。
        if(repos==null||repos.size()==0){
            return null;
        }
        if(repos.size()==1){
            return repos.get(0);
        }
        return doSelect(repos);
    }

    public abstract String doSelect(List<String> repos);
}
