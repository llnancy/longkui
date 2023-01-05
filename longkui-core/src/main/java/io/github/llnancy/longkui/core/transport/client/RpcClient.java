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

package io.github.llnancy.longkui.core.transport.client;

import io.github.llnancy.longkui.core.extension.SPI;
import io.github.llnancy.longkui.core.protocol.RpcProtocol;
import io.github.llnancy.longkui.core.protocol.RpcRequest;

import java.net.InetSocketAddress;

/**
 * rpc client interface
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/15
 */
@SPI
public interface RpcClient {

    /**
     * invoke
     *
     * @param rpcProtocol  protocol
     * @param localAddress InetSocketAddress
     */
    void invoke(RpcProtocol<RpcRequest> rpcProtocol, InetSocketAddress localAddress);

    /**
     * invoke
     *
     * @param rpcProtocol protocol
     * @param host        host
     * @param port        port
     */
    default void invoke(RpcProtocol<RpcRequest> rpcProtocol, String host, int port) {
        invoke(rpcProtocol, new InetSocketAddress(host, port));
    }

    /**
     * 销毁
     */
    void destroy();
}
