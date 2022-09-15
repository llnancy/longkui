package com.sunchaser.shushan.rpc.core.transport.server;

import com.sunchaser.shushan.rpc.core.extension.SPI;

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
}
