package com.sunchaser.shushan.rpc.core.test.proxy.jmh.impl;

import com.sunchaser.shushan.rpc.core.config.RpcApplicationConfig;
import com.sunchaser.shushan.rpc.core.config.RpcServiceConfig;
import com.sunchaser.shushan.rpc.core.proxy.DynamicProxy;
import com.sunchaser.shushan.rpc.core.test.proxy.jmh.JMHProxyInvokeHandler;

import java.lang.reflect.Proxy;

/**
 * a dynamic proxy implementation based on JDK
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/15
 */
public class JMHJdkDynamicProxy extends JMHAbstractDynamicProxy {

    private static final DynamicProxy INSTANCE = new JMHJdkDynamicProxy();

    public static DynamicProxy getInstance() {
        return INSTANCE;
    }

    /**
     * doCreateProxyInstance
     *
     * @param rpcApplicationConfig rpc framework config
     * @return proxy object
     */
    @Override
    protected Object doCreateProxyInstance(RpcApplicationConfig rpcApplicationConfig) {
        RpcServiceConfig rpcServiceConfig = rpcApplicationConfig.getRpcServiceConfig();
        Class<?> clazz = rpcServiceConfig.getTargetClass();
        if (!clazz.isInterface()) {
            // throw new IllegalArgumentException(clazz.getName() + " is not an interface");
            return JMHCglibDynamicProxy.getInstance().createProxyInstance(rpcApplicationConfig);
        }
        return Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class[]{clazz},
                new JMHProxyInvokeHandler(rpcApplicationConfig)
        );
    }
}
