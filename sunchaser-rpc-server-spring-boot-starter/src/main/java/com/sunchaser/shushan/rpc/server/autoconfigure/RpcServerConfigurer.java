package com.sunchaser.shushan.rpc.server.autoconfigure;

import com.sunchaser.shushan.rpc.core.config.RpcServerConfig;
import com.sunchaser.shushan.rpc.core.config.ThreadPoolConfig;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.util.Assert;

import java.util.Objects;

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
                .whenNonNull()
                .to(rpcServerConfig::setHost);
        map.from(rpcServerProperties::getPort)
                .whenNonNull()
                .to(rpcServerConfig::setPort);
        map.from(rpcServerProperties::getIoThreads)
                .whenNonNull()
                .to(rpcServerConfig::setIoThreads);
        configureRequestExecutor(rpcServerConfig, map);
        map.from(rpcServerProperties::getRpcServer)
                .whenNonNull()
                .to(rpcServerConfig::setRpcServer);
        map.from(rpcServerProperties::getRegistry)
                .whenNonNull()
                .to(rpcServerConfig::setRegistry);
    }

    private void configureRequestExecutor(RpcServerConfig rpcServerConfig, PropertyMapper map) {
        ThreadPoolConfig properties = rpcServerProperties.getRequestExecutorConfig();
        if (Objects.isNull(properties)) {
            return;
        }
        ThreadPoolConfig config = rpcServerConfig.getRequestExecutorConfig();
        map.from(properties::getThreadNameIdentifier)
                .whenNonNull()
                .to(config::setThreadNameIdentifier);
        map.from(properties::getCorePoolSize)
                .whenNonNull()
                .to(config::setCorePoolSize);
        map.from(properties::getMaximumPoolSize)
                .whenNonNull()
                .to(config::setMaximumPoolSize);
        map.from(properties::getKeepAliveTime)
                .whenNonNull()
                .to(config::setKeepAliveTime);
        map.from(properties.getUnit())
                .whenNonNull()
                .to(config::setUnit);
        map.from(properties.getWorkQueueCapacity())
                .whenNonNull()
                .to(config::setWorkQueueCapacity);
        map.from(properties.getWorkQueue())
                .whenNonNull()
                .to(config::setWorkQueue);
    }
}
