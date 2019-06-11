package com.lxw.personal;

/**
 * 2019/6/11/0011
 * Create by 刘仙伟
 */
@RpcService(value = HelloService.class,version = "V1.0")
public class HelloServiceImpl implements HelloService{
    @Override
    public String sayHello(String content) {
        System.out.println("【V1.0】request in sayHello:"+content);
        return "【V1.0】Say Hello:"+content;
    }

    @Override
    public String saveUser(User user) {
        System.out.println("【V1.0】request in saveUser:"+user);
        return "【V1.0】SUCCESS";
    }
}
