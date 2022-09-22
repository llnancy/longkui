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

package com.sunchaser.shushan.rpc.core.test.proxy.jmh;

import com.sunchaser.shushan.rpc.core.config.RpcClientConfig;
import com.sunchaser.shushan.rpc.core.config.RpcServiceConfig;
import javassist.util.proxy.MethodHandler;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * JMH基准测试 method invoke handler
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/8/24
 */
@Getter
@Slf4j
public class JMHProxyInvokeHandler implements InvocationHandler, MethodInterceptor, MethodHandler {

    private final RpcClientConfig rpcClientConfig;

    private final RpcServiceConfig rpcServiceConfig;

    public JMHProxyInvokeHandler(RpcClientConfig rpcClientConfig, RpcServiceConfig rpcServiceConfig) {
        this.rpcClientConfig = rpcClientConfig;
        this.rpcServiceConfig = rpcServiceConfig;
    }

    public Object benchmarkInvoke(Object[] args) {
        return args[0];
    }

    /**
     * JDK动态代理
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // LOGGER.info("jdk benchmarkInvoke, args: {}", Arrays.toString(args));
        return benchmarkInvoke(args);
    }

    /**
     * cglib动态代理
     */
    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        // LOGGER.info("cglib benchmarkInvoke, args: {}", Arrays.toString(args));
        return benchmarkInvoke(args);
    }

    /**
     * javassist动态代理
     */
    @Override
    public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
        // LOGGER.info("javassist benchmarkInvoke, args: {}", Arrays.toString(args));
        return benchmarkInvoke(args);
    }

    /**
     * byte buddy动态代理
     */
    @RuntimeType
    public Object byteBuddyInvoke(@This Object proxy, @Origin Method method, @AllArguments @RuntimeType Object[] args) throws Throwable {
        // LOGGER.info("byte-buddy benchmarkInvoke, args: {}", Arrays.toString(args));
        return benchmarkInvoke(args);
    }
}
