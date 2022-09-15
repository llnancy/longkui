package com.sunchaser.shushan.rpc.core.proxy.impl;

import com.google.common.collect.Maps;
import com.sunchaser.shushan.rpc.core.config.RpcServiceConfig;
import com.sunchaser.shushan.rpc.core.proxy.RpcDynamicProxy;

import java.util.concurrent.ConcurrentMap;

/**
 * an abstract rpc dynamic proxy implementation
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/15
 */
public abstract class AbstractRpcDynamicProxy implements RpcDynamicProxy {

    private final ConcurrentMap<RpcServiceConfig, Object> PROXY_CACHE = Maps.newConcurrentMap();

    /**
     * 根据RpcServiceConfig创建并获取代理对象
     *
     * @param rpcServiceConfig rpc service config
     * @return 代理对象
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T createProxyInstance(RpcServiceConfig rpcServiceConfig) {
        return (T) PROXY_CACHE.computeIfAbsent(rpcServiceConfig, proxy -> doCreateProxyInstance(rpcServiceConfig));
    }

    /**
     * doCreateProxyInstance
     *
     * @param rpcServiceConfig rpc service config
     * @return proxy object
     */
    protected abstract Object doCreateProxyInstance(RpcServiceConfig rpcServiceConfig);
}
