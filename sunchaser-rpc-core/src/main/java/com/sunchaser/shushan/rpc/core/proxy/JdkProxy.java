package com.sunchaser.shushan.rpc.core.proxy;

import com.sunchaser.shushan.rpc.core.registry.Registry;
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
public class JdkProxy extends AbstractProxy implements InvocationHandler {

    public JdkProxy(String serviceName, Registry registry) {
        super(serviceName, registry);
    }

    public JdkProxy(String serviceName, Registry registry, int timeout) {
        super(serviceName, registry, timeout);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return proxyInvoke(method, args);
    }
}
