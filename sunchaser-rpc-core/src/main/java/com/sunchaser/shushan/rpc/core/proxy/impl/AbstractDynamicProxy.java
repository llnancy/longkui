package com.sunchaser.shushan.rpc.core.proxy.impl;

import com.google.common.collect.Maps;
import com.sunchaser.shushan.rpc.core.config.RpcApplicationConfig;
import com.sunchaser.shushan.rpc.core.proxy.DynamicProxy;

import java.util.concurrent.ConcurrentMap;

/**
 * an abstract dynamic proxy implementation
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/15
 */
public abstract class AbstractDynamicProxy implements DynamicProxy {

    private final ConcurrentMap<RpcApplicationConfig, Object> PROXY_CACHE = Maps.newConcurrentMap();

    /**
     * 根据RpcServiceConfig创建并获取代理对象
     *
     * @param rpcApplicationConfig rpc framework config
     * @return 代理对象
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T createProxyInstance(RpcApplicationConfig rpcApplicationConfig) {
        return (T) PROXY_CACHE.computeIfAbsent(rpcApplicationConfig, proxy -> doCreateProxyInstance(rpcApplicationConfig));
    }

    /**
     * doCreateProxyInstance
     *
     * @param rpcApplicationConfig rpc framework config
     * @return proxy object
     */
    protected abstract Object doCreateProxyInstance(RpcApplicationConfig rpcApplicationConfig);
}
