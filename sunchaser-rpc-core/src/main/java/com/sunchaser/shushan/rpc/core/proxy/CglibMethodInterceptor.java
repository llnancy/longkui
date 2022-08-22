package com.sunchaser.shushan.rpc.core.proxy;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * 基于cglib的方法调用拦截实现
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/8/11
 */
public class CglibMethodInterceptor extends BaseMethodInterceptor implements MethodInterceptor {

    public CglibMethodInterceptor(Class<?> target) {
        super(target);
    }

    public CglibMethodInterceptor(Class<?> target, int timeout) {
        super(target, timeout);
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        return proxyInvoke(method, args);
    }
}
