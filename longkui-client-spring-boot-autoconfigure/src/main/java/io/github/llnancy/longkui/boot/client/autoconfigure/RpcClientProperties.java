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

package io.github.llnancy.longkui.boot.client.autoconfigure;

import io.github.llnancy.longkui.boot.common.ThreadPoolProperties;
import io.github.llnancy.longkui.core.config.RpcProtocolConfig;
import io.github.llnancy.longkui.core.config.RpcServiceConfig;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * rpc client properties
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/21
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "longkui.client")
public class RpcClientProperties {

    /**
     * connection timeout
     */
    private Integer connectionTimeout;

    /**
     * io threads
     */
    private Integer ioThreads;

    /**
     * callback type thread pool config
     */
    @NestedConfigurationProperty
    private ThreadPoolProperties callbackThreadPool;

    /**
     * dynamic proxy
     */
    private String dynamicProxy;

    /**
     * registry
     */
    private String registry;

    /**
     * load balancer
     */
    private String loadBalancer;

    /**
     * rpc client (service consumer)
     */
    private String rpcClient;

    /**
     * rpc protocol config
     */
    @NestedConfigurationProperty
    private RpcProtocolConfig rpcProtocol;

    /**
     * rpc service config
     */
    @NestedConfigurationProperty
    private RpcServiceConfig rpcService;

}
