package com.sunchaser.shushan.rpc.core.proxy.impl;

import com.sunchaser.shushan.rpc.core.config.RpcFrameworkConfig;
import com.sunchaser.shushan.rpc.core.config.RpcServiceConfig;
import com.sunchaser.shushan.rpc.core.proxy.DynamicProxy;
import com.sunchaser.shushan.rpc.core.proxy.ProxyInvokeHandler;
import net.sf.cglib.proxy.Enhancer;

/**
 * a dynamic proxy implementation based on Cglib
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/15
 */
public class CglibDynamicProxy extends AbstractDynamicProxy {

    private static final DynamicProxy INSTANCE = new CglibDynamicProxy();

    public static DynamicProxy getInstance() {
        return INSTANCE;
    }

    /**
     * doCreateProxyInstance
     *
     * @param rpcFrameworkConfig rpc framework config
     * @return proxy object
     */
    @Override
    protected Object doCreateProxyInstance(RpcFrameworkConfig rpcFrameworkConfig) {
        RpcServiceConfig rpcServiceConfig = rpcFrameworkConfig.getRpcServiceConfig();
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(rpcServiceConfig.getTargetClass());
        enhancer.setCallback(new ProxyInvokeHandler(rpcFrameworkConfig));
        return enhancer.create();
    }
}