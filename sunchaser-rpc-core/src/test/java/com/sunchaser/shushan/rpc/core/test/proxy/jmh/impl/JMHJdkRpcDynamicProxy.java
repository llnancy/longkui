package com.sunchaser.shushan.rpc.core.test.proxy.jmh.impl;

import com.sunchaser.shushan.rpc.core.config.RpcServiceConfig;
import com.sunchaser.shushan.rpc.core.proxy.RpcDynamicProxy;
import com.sunchaser.shushan.rpc.core.test.proxy.jmh.JMHProxyInvokeHandler;

import java.lang.reflect.Proxy;

/**
 * a rpc dynamic proxy implementation based on JDK
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/15
 */
public class JMHJdkRpcDynamicProxy extends JMHAbstractRpcDynamicProxy {

    private static final RpcDynamicProxy INSTANCE = new JMHJdkRpcDynamicProxy();

    public static RpcDynamicProxy getInstance() {
        return INSTANCE;
    }

    /**
     * doCreateProxyInstance
     *
     * @param rpcServiceConfig rpc service config
     * @return proxy object
     */
    @Override
    protected Object doCreateProxyInstance(RpcServiceConfig rpcServiceConfig) {
        Class<?> clazz = rpcServiceConfig.getTargetClass();
        if (!clazz.isInterface()) {
            // throw new IllegalArgumentException(clazz.getName() + " is not an interface");
            return JMHCglibRpcDynamicProxy.getInstance().createProxyInstance(rpcServiceConfig);
        }
        return Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class[]{clazz},
                new JMHProxyInvokeHandler(rpcServiceConfig)
        );
    }
}
