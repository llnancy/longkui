package com.sunchaser.shushan.rpc.core.proxy;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 基于JDK的动态代理实现
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/15
 */
@Slf4j
public class JdkProxy extends BaseProxy implements InvocationHandler {

    public JdkProxy(String serviceName) {
        super(serviceName);
    }

    public JdkProxy(String serviceName, int timeout) {
        super(serviceName, timeout);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return proxyInvoke(method, args);
    }
}
