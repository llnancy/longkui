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

package io.github.llnancy.longkui.boot.server.autoconfigure;

import io.github.llnancy.longkui.boot.common.Configurers;
import io.github.llnancy.longkui.core.config.RpcServerConfig;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.util.Assert;

/**
 * rpc server configurer
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/22
 */
public class RpcServerConfigurer {

    private final RpcServerProperties rpcServerProperties;

    public RpcServerConfigurer(RpcServerProperties rpcServerProperties) {
        Assert.notNull(rpcServerProperties, "RpcServerProperties must not be null");
        this.rpcServerProperties = rpcServerProperties;
    }

    /**
     * Configure the specified {@link RpcServerConfig}. The config can be further tuned
     * and default configs can be overridden.
     *
     * @param rpcServerConfig the {@link RpcServerConfig} instance to configure
     */
    public void configure(RpcServerConfig rpcServerConfig) {
        PropertyMapper map = PropertyMapper.get();
        map.from(rpcServerProperties::getHost)
                .whenHasText()
                .to(rpcServerConfig::setHost);
        map.from(rpcServerProperties::getPort)
                .whenNonNull()
                .to(rpcServerConfig::setPort);
        map.from(rpcServerProperties::getIoThreads)
                .whenNonNull()
                .to(rpcServerConfig::setIoThreads);
        map.from(rpcServerProperties::getRpcServer)
                .whenHasText()
                .to(rpcServerConfig::setRpcServer);
        map.from(rpcServerProperties::getRegistry)
                .whenHasText()
                .to(rpcServerConfig::setRegistry);
        Configurers.configureThreadPool(
                map,
                rpcServerProperties::getRequestExecutor,
                rpcServerConfig::getRequestExecutorConfig
        );
    }
}
