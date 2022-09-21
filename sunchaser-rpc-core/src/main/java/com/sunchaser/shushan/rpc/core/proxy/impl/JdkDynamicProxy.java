package com.sunchaser.shushan.rpc.core.proxy.impl;

import com.sunchaser.shushan.rpc.core.config.RpcClientConfig;
import com.sunchaser.shushan.rpc.core.config.RpcServiceConfig;
import com.sunchaser.shushan.rpc.core.proxy.DynamicProxy;
import com.sunchaser.shushan.rpc.core.proxy.ProxyInvokeHandler;

import java.lang.reflect.Proxy;

/**
 * a dynamic proxy implementation based on JDK
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/15
 */
public class JdkDynamicProxy extends AbstractDynamicProxy {

    private static final DynamicProxy INSTANCE = new JdkDynamicProxy();

    public static DynamicProxy getInstance() {
        return INSTANCE;
    }

    /**
     * doCreateProxyInstance
     *
     * @param rpcClientConfig rpc client config
     * @return proxy object
     */
    @Override
    protected Object doCreateProxyInstance(RpcClientConfig rpcClientConfig) {
        RpcServiceConfig rpcServiceConfig = rpcClientConfig.getRpcServiceConfig();
        Class<?> clazz = rpcServiceConfig.getTargetClass();
        if (!clazz.isInterface()) {
            // throw new IllegalArgumentException(clazz.getName() + " is not an interface");
            return CglibDynamicProxy.getInstance().createProxyInstance(rpcClientConfig);
        }
        return Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class[]{clazz},
                new ProxyInvokeHandler(rpcClientConfig)
        );
    }
}
