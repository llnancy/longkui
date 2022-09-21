package com.sunchaser.shushan.rpc.core.config;

import com.sunchaser.shushan.rpc.core.common.Constants;
import com.sunchaser.shushan.rpc.core.transport.server.RpcServer;
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
     * biz thread pool config
     */
    private ThreadPoolConfig threadPoolConfig;

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
        this.threadPoolConfig = ThreadPoolConfig.createDefaultConfig();
        this.rpcServer = Constants.NETTY;
        this.registry = Constants.DEFAULT_REGISTRY;
    }

    public static RpcServerConfig createDefaultConfig() {
        return new RpcServerConfig();
    }
}
