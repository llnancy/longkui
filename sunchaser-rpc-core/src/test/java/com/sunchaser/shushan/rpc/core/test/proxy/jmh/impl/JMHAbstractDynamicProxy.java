package com.sunchaser.shushan.rpc.core.test.proxy.jmh.impl;

import com.sunchaser.shushan.rpc.core.config.RpcClientConfig;
import com.sunchaser.shushan.rpc.core.config.RpcServiceConfig;
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
     * 根据 {@link RpcClientConfig} 和 {@link RpcServiceConfig} 创建并获取代理对象
     *
     * @param rpcClientConfig  rpc client config
     * @param rpcServiceConfig rpc service config
     * @param <T>              代理对象的类型
     * @return 代理对象
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T createProxyInstance(RpcClientConfig rpcClientConfig, RpcServiceConfig rpcServiceConfig) {
        return (T) doCreateProxyInstance(rpcClientConfig, rpcServiceConfig);
    }

    /**
     * doCreateProxyInstance
     *
     * @param rpcClientConfig  rpc client config
     * @param rpcServiceConfig rpc service config
     * @return proxy object
     */
    protected abstract Object doCreateProxyInstance(RpcClientConfig rpcClientConfig, RpcServiceConfig rpcServiceConfig);
}
