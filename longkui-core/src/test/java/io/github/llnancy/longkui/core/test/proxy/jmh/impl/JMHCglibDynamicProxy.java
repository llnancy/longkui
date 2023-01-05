/*
 * Copyright 2022 LongKui
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.llnancy.longkui.core.test.proxy.jmh.impl;

import io.github.llnancy.longkui.core.config.RpcClientConfig;
import io.github.llnancy.longkui.core.config.RpcServiceConfig;
import io.github.llnancy.longkui.core.proxy.DynamicProxy;
import io.github.llnancy.longkui.core.test.proxy.jmh.JMHDynamicProxyHandler;
import net.sf.cglib.proxy.Enhancer;

/**
 * a dynamic proxy implementation based on Cglib
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/15
 */
public class JMHCglibDynamicProxy extends JMHAbstractDynamicProxy {

    private static final DynamicProxy INSTANCE = new JMHCglibDynamicProxy();

    public static DynamicProxy getInstance() {
        return INSTANCE;
    }

    /**
     * doCreateProxyInstance
     *
     * @param rpcClientConfig  rpc client config
     * @param rpcServiceConfig rpc service config
     * @return proxy object
     */
    @Override
    protected Object doCreateProxyInstance(RpcClientConfig rpcClientConfig, RpcServiceConfig rpcServiceConfig) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(rpcServiceConfig.getTargetClass());
        enhancer.setCallback(new JMHDynamicProxyHandler(rpcClientConfig, rpcServiceConfig));
        return enhancer.create();
    }
}
