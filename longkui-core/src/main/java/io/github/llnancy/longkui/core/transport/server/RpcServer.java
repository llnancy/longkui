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

package io.github.llnancy.longkui.core.transport.server;

import io.github.llnancy.longkui.core.extension.SPI;

import java.net.InetSocketAddress;

/**
 * rpc server interface
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/8/16
 */
@SPI
public interface RpcServer {

    int DEFAULT_PORT = 1234;

    /**
     * 服务端启动
     *
     * @param localAddress InetSocketAddress
     */
    void start(InetSocketAddress localAddress);

    /**
     * 服务端启动
     *
     * @param port port
     */
    default void start(int port) {
        start(new InetSocketAddress(port));
    }

    /**
     * 服务端启动
     */
    default void start() {
        start(DEFAULT_PORT);
    }

    /**
     * 销毁
     */
    void destroy();
}
