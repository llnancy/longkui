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

package com.sunchaser.shushan.rpc.boot.client.autoconfigure;

import com.sunchaser.shushan.rpc.boot.common.Configurers;
import com.sunchaser.shushan.rpc.core.config.RpcClientConfig;
import com.sunchaser.shushan.rpc.core.config.RpcProtocolConfig;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.util.Assert;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * rpc client configurer
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/22
 */
public class RpcClientConfigurer {

    private final RpcClientProperties rpcClientProperties;

    public RpcClientConfigurer(RpcClientProperties rpcClientProperties) {
        Assert.notNull(rpcClientProperties, "RpcClientProperties must not be null");
        this.rpcClientProperties = rpcClientProperties;
    }

    /**
     * Configure the specified {@link RpcClientConfig}. The config can be further tuned
     * and default configs can be overridden.
     *
     * @param rpcClientConfig the {@link RpcClientConfig} instance to configure
     */
    public void configure(RpcClientConfig rpcClientConfig) {
        PropertyMapper map = PropertyMapper.get();
        {// client connection timeout and io threads
            map.from(rpcClientProperties::getConnectionTimeout)
                    .whenNonNull()
                    .to(rpcClientConfig::setConnectionTimeout);
            map.from(rpcClientProperties::getIoThreads)
                    .whenNonNull()
                    .to(rpcClientConfig::setIoThreads);
        }
        {// rpc components
            map.from(rpcClientProperties::getDynamicProxy)
                    .whenHasText()
                    .to(rpcClientConfig::setDynamicProxy);
            map.from(rpcClientProperties::getRegistry)
                    .whenHasText()
                    .to(rpcClientConfig::setRegistry);
            map.from(rpcClientProperties::getLoadBalancer)
                    .whenHasText()
                    .to(rpcClientConfig::setLoadBalancer);
            map.from(rpcClientProperties::getRpcClient)
                    .whenHasText()
                    .to(rpcClientConfig::setRpcClient);
        }
        {// rpc protocol info
            configureRpcProtocol(
                    map,
                    rpcClientProperties::getRpcProtocol,
                    rpcClientConfig::getRpcProtocolConfig
            );
        }
        {// callback executor thread pool
            Configurers.configureThreadPool(
                    map,
                    rpcClientProperties::getCallbackThreadPool,
                    rpcClientConfig::getCallbackThreadPoolConfig
            );
        }
    }

    private void configureRpcProtocol(PropertyMapper map,
                                      Supplier<RpcProtocolConfig> source,
                                      Supplier<RpcProtocolConfig> target) {
        Assert.notNull(source, "Supplier source must not be null");
        Assert.notNull(target, "Supplier target must not be null");
        RpcProtocolConfig properties = source.get();
        RpcProtocolConfig config = target.get();
        if (Objects.isNull(properties)) {
            return;
        }
        map.from(properties::getProtocolVersion)
                .whenNonNull()
                .to(config::setProtocolVersion);
        map.from(properties::getSerializer)
                .whenHasText()
                .to(config::setSerializer);
        map.from(properties::getCompressor)
                .whenHasText()
                .to(config::setCompressor);
        map.from(properties::getSequenceIdGenerator)
                .whenHasText()
                .to(config::setSequenceIdGenerator);
    }
}
