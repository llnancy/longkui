package com.sunchaser.shushan.rpc.core.proxy;

import com.sunchaser.shushan.rpc.core.registry.Registry;

import java.lang.reflect.Proxy;

/**
 * rpc proxy
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/17
 */
public class RpcProxy {

    @SuppressWarnings("unchecked")
    public static <T> T newInstance(Class<T> clazz, Registry registry) {
        return (T) Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class[]{clazz},
                new RpcInvocationHandler(clazz.getName(), registry)
        );
    }
}
