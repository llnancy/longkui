package com.sunchaser.shushan.rpc.core.transport;

import com.sunchaser.shushan.rpc.core.common.Constants;

import java.net.InetSocketAddress;

/**
 * rpc server interface
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/8/16
 */
public interface RpcServer {

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
        start(Constants.DEFAULT_PORT);
    }
}
