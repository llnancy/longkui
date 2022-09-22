/*
 * Copyright 2022 SunChaser
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sunchaser.shushan.rpc.core.proxy.impl;

import com.google.common.collect.Maps;
import com.sunchaser.shushan.rpc.core.config.RpcClientConfig;
import com.sunchaser.shushan.rpc.core.config.RpcServiceConfig;
import com.sunchaser.shushan.rpc.core.proxy.DynamicProxy;

import java.util.concurrent.ConcurrentMap;

/**
 * an abstract dynamic proxy implementation
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/15
 */
public abstract class AbstractDynamicProxy implements DynamicProxy {

    private final ConcurrentMap<RpcServiceConfig, Object> PROXY_CACHE = Maps.newConcurrentMap();

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
        return (T) PROXY_CACHE.computeIfAbsent(rpcServiceConfig, proxy -> doCreateProxyInstance(rpcClientConfig, rpcServiceConfig));
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
