package com.lxw.personal;

import java.io.Serializable;

/**
 * 2019/6/11/0011
 * Create by 刘仙伟
 */
public class RpcRequest implements Serializable {
    private String className;
    private String methodName;
    private Object[] parameters;

    private String version;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }
}
