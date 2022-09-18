package com.sunchaser.shushan.rpc.core.config;

import com.sunchaser.shushan.rpc.core.common.Constants;
import com.sunchaser.shushan.rpc.core.transport.server.RpcServer;
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
public class RpcServerConfig {

    private String host;

    private Integer port;

    private Integer ioThreads;

    private ThreadPoolConfig threadPoolConfig;

    @SneakyThrows
    public RpcServerConfig() {
        this.host = InetAddress.getLocalHost().getHostAddress();
        this.port = RpcServer.DEFAULT_PORT;
        this.ioThreads = Constants.DEFAULT_IO_THREADS;
        this.threadPoolConfig = ThreadPoolConfig.createDefaultConfig();
    }

    public RpcServerConfig(String host, Integer port, Integer ioThreads, ThreadPoolConfig threadPoolConfig) {
        this.host = host;
        this.port = port;
        this.ioThreads = ioThreads;
        this.threadPoolConfig = threadPoolConfig;
    }

    public static RpcServerConfig createDefaultConfig() {
        return new RpcServerConfig();
    }
}
