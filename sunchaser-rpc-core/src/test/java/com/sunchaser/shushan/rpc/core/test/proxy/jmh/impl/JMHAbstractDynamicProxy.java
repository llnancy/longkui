package com.sunchaser.shushan.rpc.core.test.proxy.jmh.impl;

import com.sunchaser.shushan.rpc.core.config.RpcFrameworkConfig;
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
     * @param rpcFrameworkConfig rpc framework config
     * @return 代理对象
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T createProxyInstance(RpcFrameworkConfig rpcFrameworkConfig) {
        return (T) doCreateProxyInstance(rpcFrameworkConfig);
    }

    /**
     * doCreateProxyInstance
     *
     * @param rpcFrameworkConfig rpc framework config
     * @return proxy object
     */
    protected abstract Object doCreateProxyInstance(RpcFrameworkConfig rpcFrameworkConfig);
}
