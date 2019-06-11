package com.lxw.personal;

/**
 * 2019/6/11/0011
 * Create by 刘仙伟
 */
public interface HelloService {
    String sayHello(String content);

    /**
     * 保存用户
     * @param user
     * @return
     */
    String saveUser(User user);
}
