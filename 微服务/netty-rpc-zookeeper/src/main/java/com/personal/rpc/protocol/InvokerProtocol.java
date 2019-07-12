package com.personal.rpc.protocol;

import lombok.Data;

import java.io.Serializable;

/**
 * 2019/6/16/0016
 * Create by 刘仙伟
 */
@Data
public class InvokerProtocol implements Serializable {
    private String className;
    private String methodName;
    private Class<?> [] parames;
    private Object[] values;

}
