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

package com.sunchaser.shushan.rpc.core.test.proxy.jmh.impl;

import com.sunchaser.shushan.rpc.core.config.RpcClientConfig;
import com.sunchaser.shushan.rpc.core.config.RpcServiceConfig;
import com.sunchaser.shushan.rpc.core.proxy.DynamicProxy;
import com.sunchaser.shushan.rpc.core.test.proxy.jmh.JMHProxyInvokeHandler;
import lombok.SneakyThrows;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

/**
 * a dynamic proxy implementation based on Byte Buddy
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/15
 */
public class JMHByteBuddyDynamicProxy extends JMHAbstractDynamicProxy {

    private static final DynamicProxy INSTANCE = new JMHByteBuddyDynamicProxy();

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
    @SuppressWarnings("all")
    @SneakyThrows
    @Override
    protected Object doCreateProxyInstance(RpcClientConfig rpcClientConfig, RpcServiceConfig rpcServiceConfig) {
        Class<?> clazz = rpcServiceConfig.getTargetClass();
        return new ByteBuddy().subclass(clazz)
                .method(ElementMatchers.isDeclaredBy(clazz))
                .intercept(MethodDelegation.to(new JMHProxyInvokeHandler(rpcClientConfig, rpcServiceConfig)))
                .make()
                .load(clazz.getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                .getLoaded()
                .getDeclaredConstructor()
                .newInstance();
    }
}
