/*
 * Copyright 2023 LongKui
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

package io.github.llnancy.longkui.core.test;

import io.github.llnancy.longkui.core.config.RpcServerConfig;
import io.github.llnancy.longkui.core.config.RpcServiceConfig;
import io.github.llnancy.longkui.core.extension.ExtensionLoader;
import io.github.llnancy.longkui.core.provider.ServiceProvider;
import io.github.llnancy.longkui.core.provider.impl.InMemoryServiceProvider;
import io.github.llnancy.longkui.core.registry.Registry;
import io.github.llnancy.longkui.core.registry.ServiceMetaData;
import io.github.llnancy.longkui.core.transport.server.RpcServer;

/**
 * rpc provider
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/17
 */
public class Provider {

    public static void main(String[] args) throws Exception {
        RpcServerConfig rpcServerConfig = RpcServerConfig.createDefaultConfig();
        RpcServiceConfig rpcServiceConfig = RpcServiceConfig.createDefaultConfig(HelloService.class);
        String serviceKey = rpcServiceConfig.getRpcServiceKey();

        // service provider
        ServiceProvider serviceProvider = InMemoryServiceProvider.getInstance();
        serviceProvider.registerProvider(serviceKey, new HelloServiceImpl());

        // registry
        ServiceMetaData serviceMetaData = ServiceMetaData.builder()
                .serviceKey(serviceKey)
                .host(rpcServerConfig.getHost())
                .port(rpcServerConfig.getPort())
                .weight(rpcServiceConfig.getWeight())
                .warmup(rpcServiceConfig.getWarmup())
                .timestamp(System.currentTimeMillis())
                .build();
        Registry registry = ExtensionLoader.getExtensionLoader(Registry.class).getExtension(rpcServerConfig.getRegistry());
        registry.register(serviceMetaData);

        // rpc server
        RpcServer rpcServer = ExtensionLoader.getExtensionLoader(RpcServer.class).getExtension(rpcServerConfig.getRpcServer());
        rpcServer.start();
    }
}
