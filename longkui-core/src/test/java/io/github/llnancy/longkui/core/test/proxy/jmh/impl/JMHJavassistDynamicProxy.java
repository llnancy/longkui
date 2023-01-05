/*
 * Copyright 2022 LongKui
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

package io.github.llnancy.longkui.core.test.proxy.jmh.impl;

import io.github.llnancy.longkui.core.config.RpcClientConfig;
import io.github.llnancy.longkui.core.config.RpcServiceConfig;
import io.github.llnancy.longkui.core.proxy.DynamicProxy;
import io.github.llnancy.longkui.core.test.proxy.jmh.JMHDynamicProxyHandler;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;
import lombok.SneakyThrows;

/**
 * a dynamic proxy implementation based on javassist
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/15
 */
public class JMHJavassistDynamicProxy extends JMHAbstractDynamicProxy {

    private static final DynamicProxy INSTANCE = new JMHJavassistDynamicProxy();

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
    @SneakyThrows
    @Override
    protected Object doCreateProxyInstance(RpcClientConfig rpcClientConfig, RpcServiceConfig rpcServiceConfig) {
        ProxyFactory factory = new ProxyFactory();
        // 设置接口
        factory.setInterfaces(new Class[]{rpcServiceConfig.getTargetClass()});
        // 设置拦截方法过滤器。设置哪些方法调用需要被拦截
        factory.setFilter(m -> true);
        Class<?> proxyClass = factory.createClass();
        ProxyObject proxyObject = (ProxyObject) proxyClass.getDeclaredConstructor()
                .newInstance();
        proxyObject.setHandler(new JMHDynamicProxyHandler(rpcClientConfig, rpcServiceConfig));
        return proxyObject;
    }
}
