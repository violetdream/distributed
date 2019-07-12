package com.personal.rpc.consumer.discovery;

import java.util.List;
import java.util.Random;

/**
 * 2019/7/12/0012
 * Create by 刘仙伟
 */
public class RandomLoadBalance extends AbstractLoadBalance{

    @Override
    public String doSelect(List<String> repos) {
        int size=repos.size();
        Random random=new Random();
        return repos.get(random.nextInt(size));
    }
}
