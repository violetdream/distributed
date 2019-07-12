package com.personal.rpc.consumer.discovery;

import java.util.List;

/**
 * 2019/7/12/0012
 * Create by 刘仙伟
 */
public interface LoadBalanceStrategy {
    String selectHost(List<String> repos);
}
