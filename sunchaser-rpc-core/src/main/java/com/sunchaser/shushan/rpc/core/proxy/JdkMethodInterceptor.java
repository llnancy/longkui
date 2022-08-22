package com.sunchaser.shushan.rpc.core.proxy;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 基于JDK的方法调用拦截实现
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/15
 */
@Slf4j
public class JdkMethodInterceptor extends BaseMethodInterceptor implements InvocationHandler {

    public JdkMethodInterceptor(Class<?> target) {
        super(target);
    }

    public JdkMethodInterceptor(Class<?> target, int timeout) {
        super(target, timeout);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return proxyInvoke(method, args);
    }
}
