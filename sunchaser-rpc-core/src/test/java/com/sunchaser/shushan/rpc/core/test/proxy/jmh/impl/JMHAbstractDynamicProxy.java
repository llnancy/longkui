package com.sunchaser.shushan.rpc.core.test.proxy.jmh.impl;

import com.sunchaser.shushan.rpc.core.config.RpcClientConfig;
import com.sunchaser.shushan.rpc.core.proxy.DynamicProxy;

/**
 * no cache
 * an abstract dynamic proxy implementation
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/15
 */
public abstract class JMHAbstractDynamicProxy implements DynamicProxy {

    // no cache
    // private final ConcurrentMap<RpcServiceConfig, Object> PROXY_CACHE = Maps.newConcurrentMap();

    /**
     * 根据RpcServiceConfig创建并获取代理对象
     *
     * @param rpcClientConfig rpc client config
     * @return 代理对象
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T createProxyInstance(RpcClientConfig rpcClientConfig) {
        return (T) doCreateProxyInstance(rpcClientConfig);
    }

    /**
     * doCreateProxyInstance
     *
     * @param rpcClientConfig rpc client config
     * @return proxy object
     */
    protected abstract Object doCreateProxyInstance(RpcClientConfig rpcClientConfig);
}
