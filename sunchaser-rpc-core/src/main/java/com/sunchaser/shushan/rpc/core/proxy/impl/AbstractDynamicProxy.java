package com.sunchaser.shushan.rpc.core.proxy.impl;

import com.google.common.collect.Maps;
import com.sunchaser.shushan.rpc.core.config.RpcFrameworkConfig;
import com.sunchaser.shushan.rpc.core.proxy.DynamicProxy;

import java.util.concurrent.ConcurrentMap;

/**
 * an abstract rpc dynamic proxy implementation
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/15
 */
public abstract class AbstractDynamicProxy implements DynamicProxy {

    private final ConcurrentMap<RpcFrameworkConfig, Object> PROXY_CACHE = Maps.newConcurrentMap();

    /**
     * 根据RpcServiceConfig创建并获取代理对象
     *
     * @param rpcFrameworkConfig rpc framework config
     * @return 代理对象
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T createProxyInstance(RpcFrameworkConfig rpcFrameworkConfig) {
        return (T) PROXY_CACHE.computeIfAbsent(rpcFrameworkConfig, proxy -> doCreateProxyInstance(rpcFrameworkConfig));
    }

    /**
     * doCreateProxyInstance
     *
     * @param rpcFrameworkConfig rpc framework config
     * @return proxy object
     */
    protected abstract Object doCreateProxyInstance(RpcFrameworkConfig rpcFrameworkConfig);
}
