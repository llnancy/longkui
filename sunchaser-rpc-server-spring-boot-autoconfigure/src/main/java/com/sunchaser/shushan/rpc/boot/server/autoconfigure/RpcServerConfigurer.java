package com.sunchaser.shushan.rpc.boot.server.autoconfigure;

import com.sunchaser.shushan.rpc.boot.common.Configurers;
import com.sunchaser.shushan.rpc.core.config.RpcServerConfig;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.util.Assert;

/**
 * rpc server configurer
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/22
 */
public class RpcServerConfigurer {

    private final RpcServerProperties rpcServerProperties;

    public RpcServerConfigurer(RpcServerProperties rpcServerProperties) {
        Assert.notNull(rpcServerProperties, "RpcServerProperties must not be null");
        this.rpcServerProperties = rpcServerProperties;
    }

    /**
     * Configure the specified {@link RpcServerConfig}. The config can be further tuned
     * and default configs can be overridden.
     *
     * @param rpcServerConfig the {@link RpcServerConfig} instance to configure
     */
    public void configure(RpcServerConfig rpcServerConfig) {
        PropertyMapper map = PropertyMapper.get();
        map.from(rpcServerProperties::getHost)
                .whenHasText()
                .to(rpcServerConfig::setHost);
        map.from(rpcServerProperties::getPort)
                .whenNonNull()
                .to(rpcServerConfig::setPort);
        map.from(rpcServerProperties::getIoThreads)
                .whenNonNull()
                .to(rpcServerConfig::setIoThreads);
        map.from(rpcServerProperties::getRpcServer)
                .whenHasText()
                .to(rpcServerConfig::setRpcServer);
        map.from(rpcServerProperties::getRegistry)
                .whenHasText()
                .to(rpcServerConfig::setRegistry);
        Configurers.configureThreadPool(
                map,
                rpcServerProperties::getRequestExecutor,
                rpcServerConfig::getRequestExecutorConfig
        );
    }
}
