package com.sunchaser.shushan.rpc.core.proxy;

import com.sunchaser.shushan.rpc.core.registry.Registry;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * 基于cglib的动态代理实现
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/8/11
 */
public class CglibProxy extends AbstractProxy implements MethodInterceptor {

    public CglibProxy(String serviceName, Registry registry) {
        super(serviceName, registry);
    }

    public CglibProxy(String serviceName, Registry registry, int timeout) {
        super(serviceName, registry, timeout);
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        return proxyInvoke(method, args);
    }
}
