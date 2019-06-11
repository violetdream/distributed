package com.lxw.personal;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 2019/6/11/0011
 * Create by 刘仙伟
 */
@Configuration
@ComponentScan(basePackages = "com.lxw.personal")
public class SpringConfig {
    @Bean(name = "LXWRpcServer")
    public LXWRpcServer getRpcServer(){
        return new LXWRpcServer(8080);
    }
}
