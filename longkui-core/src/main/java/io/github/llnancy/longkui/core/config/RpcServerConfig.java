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

package io.github.llnancy.longkui.core.config;

import io.github.llnancy.longkui.core.common.Constants;
import io.github.llnancy.longkui.core.transport.server.RpcServer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.SneakyThrows;

import java.net.InetAddress;

/**
 * rpc server config
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/18
 */
@Data
@Builder
@AllArgsConstructor
public class RpcServerConfig {

    /**
     * default reader idle time seconds.
     */
    public static final Integer DEFAULT_READER_IDLE_TIME_SECONDS = 60;

    /**
     * server host
     */
    private String host;

    /**
     * server port
     */
    private Integer port;

    /**
     * io threads
     */
    private Integer ioThreads;

    /**
     * biz thread pool config (request executor)
     */
    private ThreadPoolConfig requestExecutorConfig;

    /**
     * rpc server (service provider)
     */
    private String rpcServer;

    /**
     * registry
     */
    private String registry;

    @SneakyThrows
    public RpcServerConfig() {
        this.host = InetAddress.getLocalHost().getHostAddress();
        this.port = RpcServer.DEFAULT_PORT;
        this.ioThreads = Constants.DEFAULT_IO_THREADS;
        this.requestExecutorConfig = ThreadPoolConfig.createDefaultConfig();
        this.rpcServer = Constants.NETTY;
        this.registry = Constants.DEFAULT_REGISTRY;
    }

    public static RpcServerConfig createDefaultConfig() {
        return new RpcServerConfig();
    }
}
