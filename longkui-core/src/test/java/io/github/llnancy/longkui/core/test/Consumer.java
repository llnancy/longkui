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

package io.github.llnancy.longkui.core.test;

import io.github.llnancy.longkui.core.config.RpcClientConfig;
import io.github.llnancy.longkui.core.config.RpcServiceConfig;
import io.github.llnancy.longkui.core.extension.ExtensionLoader;
import io.github.llnancy.longkui.core.proxy.DynamicProxy;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;

/**
 * rpc consumer
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/17
 */
@Slf4j
public class Consumer {

    public static void main(String[] args) throws Exception {
        RpcClientConfig rpcClientConfig = RpcClientConfig.createDefaultConfig();
        RpcServiceConfig rpcServiceConfig = RpcServiceConfig.createDefaultConfig(HelloService.class);
        // RpcDynamicProxy rpcDynamicProxy = JdkRpcDynamicProxy.getInstance();
        DynamicProxy dynamicProxy = ExtensionLoader.getExtensionLoader(DynamicProxy.class).getExtension(rpcClientConfig.getDynamicProxy());
        HelloService helloService = dynamicProxy.createProxyInstance(rpcClientConfig, rpcServiceConfig);
        String hello = helloService.sayHello("SunChaser");
        LOGGER.info("sayHello result: {}", hello);
        Assertions.assertEquals("Hello:" + "SunChaser", hello);
        System.in.read();
    }
}
