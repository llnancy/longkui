package com.sunchaser.shushan.rpc.core.test.proxy.jmh.impl;

import com.sunchaser.shushan.rpc.core.config.RpcClientConfig;
import com.sunchaser.shushan.rpc.core.config.RpcServiceConfig;
import com.sunchaser.shushan.rpc.core.proxy.DynamicProxy;
import com.sunchaser.shushan.rpc.core.test.proxy.jmh.JMHProxyInvokeHandler;
import net.sf.cglib.proxy.Enhancer;

/**
 * a dynamic proxy implementation based on Cglib
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/15
 */
public class JMHCglibDynamicProxy extends JMHAbstractDynamicProxy {

    private static final DynamicProxy INSTANCE = new JMHCglibDynamicProxy();

    public static DynamicProxy getInstance() {
        return INSTANCE;
    }

    /**
     * doCreateProxyInstance
     *
     * @param rpcClientConfig  rpc client config
     * @param rpcServiceConfig rpc service config
     * @return proxy object
     */
    @Override
    protected Object doCreateProxyInstance(RpcClientConfig rpcClientConfig, RpcServiceConfig rpcServiceConfig) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(rpcServiceConfig.getTargetClass());
        enhancer.setCallback(new JMHProxyInvokeHandler(rpcClientConfig, rpcServiceConfig));
        return enhancer.create();
    }
}
