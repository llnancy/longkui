package com.sunchaser.shushan.rpc.core.transport.client;

import com.sunchaser.shushan.rpc.core.protocol.RpcProtocol;
import com.sunchaser.shushan.rpc.core.protocol.RpcRequest;

import java.net.InetSocketAddress;

/**
 * rpc client interface
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/15
 */
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
}
