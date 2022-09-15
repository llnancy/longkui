package com.sunchaser.shushan.rpc.core.proxy.impl;

import com.sunchaser.shushan.rpc.core.config.RpcServiceConfig;
import com.sunchaser.shushan.rpc.core.proxy.ProxyInvokeHandler;
import com.sunchaser.shushan.rpc.core.proxy.RpcDynamicProxy;
import net.sf.cglib.proxy.Enhancer;

/**
 * a rpc dynamic proxy implementation based on Cglib
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/15
 */
public class CglibRpcDynamicProxy extends AbstractRpcDynamicProxy {

    private static final RpcDynamicProxy INSTANCE = new CglibRpcDynamicProxy();

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
        enhancer.setCallback(new ProxyInvokeHandler(rpcServiceConfig));
        return enhancer.create();
    }
}
