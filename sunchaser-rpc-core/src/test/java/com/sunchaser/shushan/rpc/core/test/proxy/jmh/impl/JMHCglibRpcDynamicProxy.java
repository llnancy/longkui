package com.sunchaser.shushan.rpc.core.test.proxy.jmh.impl;

import com.sunchaser.shushan.rpc.core.config.RpcServiceConfig;
import com.sunchaser.shushan.rpc.core.proxy.RpcDynamicProxy;
import com.sunchaser.shushan.rpc.core.test.proxy.jmh.JMHProxyInvokeHandler;
import net.sf.cglib.proxy.Enhancer;

/**
 * a rpc dynamic proxy implementation based on Cglib
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/15
 */
public class JMHCglibRpcDynamicProxy extends JMHAbstractRpcDynamicProxy {

    private static final RpcDynamicProxy INSTANCE = new JMHCglibRpcDynamicProxy();

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
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(rpcServiceConfig.getTargetClass());
        enhancer.setCallback(new JMHProxyInvokeHandler(rpcServiceConfig));
        return enhancer.create();
    }
}
