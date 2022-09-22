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

package com.sunchaser.shushan.rpc.core.test.proxy;

import com.sunchaser.shushan.rpc.core.config.RpcClientConfig;
import com.sunchaser.shushan.rpc.core.config.RpcServiceConfig;
import com.sunchaser.shushan.rpc.core.proxy.impl.ByteBuddyDynamicProxy;
import com.sunchaser.shushan.rpc.core.proxy.impl.CglibDynamicProxy;
import com.sunchaser.shushan.rpc.core.proxy.impl.JavassistDynamicProxy;
import com.sunchaser.shushan.rpc.core.proxy.impl.JdkDynamicProxy;
import com.sunchaser.shushan.rpc.core.test.HelloService;

/**
 * alibaba arthas查看动态代理生成的class
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/8/24
 */
public class LookupDynamicProxyClassTest {

    public static void main(String[] args) throws Exception {
        RpcClientConfig rpcClientConfig = RpcClientConfig.createDefaultConfig();
        RpcServiceConfig rpcServiceConfig = RpcServiceConfig.createDefaultConfig(HelloService.class);
        HelloService helloService = JdkDynamicProxy.getInstance().createProxyInstance(rpcClientConfig, rpcServiceConfig);
        HelloService cglib = CglibDynamicProxy.getInstance().createProxyInstance(rpcClientConfig, rpcServiceConfig);
        HelloService javassist = JavassistDynamicProxy.getInstance().createProxyInstance(rpcClientConfig, rpcServiceConfig);
        HelloService byteBuddy = ByteBuddyDynamicProxy.getInstance().createProxyInstance(rpcClientConfig, rpcServiceConfig);
        System.in.read();
    }
}
