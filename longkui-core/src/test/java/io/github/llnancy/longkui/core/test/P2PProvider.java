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

import io.github.llnancy.longkui.core.common.Constants;
import io.github.llnancy.longkui.core.extension.ExtensionLoader;
import io.github.llnancy.longkui.core.provider.ServiceProvider;
import io.github.llnancy.longkui.core.provider.impl.InMemoryServiceProvider;
import io.github.llnancy.longkui.core.transport.server.RpcServer;

/**
 * 点对点RPC服务提供者
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/8/18
 */
public class P2PProvider {

    public static void main(String[] args) throws Exception {
        ServiceProvider serviceProvider = InMemoryServiceProvider.getInstance();
        serviceProvider.registerProvider(HelloService.class.getName(), new HelloServiceImpl());
        RpcServer rpcServer = ExtensionLoader.getExtensionLoader(RpcServer.class).getExtension(Constants.NETTY);
        rpcServer.start();
    }
}
