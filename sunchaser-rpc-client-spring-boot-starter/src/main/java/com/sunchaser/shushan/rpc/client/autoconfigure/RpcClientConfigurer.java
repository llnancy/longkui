package com.sunchaser.shushan.rpc.client.autoconfigure;

import com.sunchaser.shushan.rpc.core.config.RpcClientConfig;
import com.sunchaser.shushan.rpc.core.config.RpcProtocolConfig;
import com.sunchaser.shushan.rpc.core.config.ThreadPoolConfig;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.util.Assert;

import java.util.Objects;

/**
 * rpc client configurer
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/22
 */
public class RpcClientConfigurer {

    private final RpcClientProperties rpcClientProperties;

    public RpcClientConfigurer(RpcClientProperties rpcClientProperties) {
        Assert.notNull(rpcClientProperties, "RpcClientProperties must not be null");
        this.rpcClientProperties = rpcClientProperties;
    }

    /**
     * Configure the specified {@link RpcClientConfig}. The config can be further tuned
     * and default configs can be overridden.
     *
     * @param rpcClientConfig the {@link RpcClientConfig} instance to configure
     */
    public void configure(RpcClientConfig rpcClientConfig) {
        PropertyMapper map = PropertyMapper.get();
        map.from(rpcClientProperties::getConnectionTimeout)
                .whenNonNull()
                .to(rpcClientConfig::setConnectionTimeout);
        map.from(rpcClientProperties::getIoThreads)
                .whenNonNull()
                .to(rpcClientConfig::setIoThreads);
        configureCallbackThreadPool(rpcClientConfig, map);
        map.from(rpcClientProperties::getDynamicProxy)
                .whenNonNull()
                .to(rpcClientConfig::setDynamicProxy);
        map.from(rpcClientProperties::getRegistry)
                .whenNonNull()
                .to(rpcClientConfig::setRegistry);
        map.from(rpcClientProperties::getLoadBalancer)
                .whenNonNull()
                .to(rpcClientConfig::setLoadBalancer);
        map.from(rpcClientProperties::getRpcClient)
                .whenNonNull()
                .to(rpcClientConfig::setRpcClient);
        configureRpcProtocol(rpcClientConfig, map);
    }

    private void configureCallbackThreadPool(RpcClientConfig rpcClientConfig, PropertyMapper map) {
        ThreadPoolConfig properties = rpcClientProperties.getCallbackThreadPoolConfig();
        if (Objects.isNull(properties)) {
            return;
        }
        ThreadPoolConfig config = rpcClientConfig.getCallbackThreadPoolConfig();
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

    private void configureRpcProtocol(RpcClientConfig rpcClientConfig, PropertyMapper map) {
        RpcProtocolConfig properties = rpcClientProperties.getRpcProtocolConfig();
        if (Objects.isNull(properties)) {
            return;
        }
        RpcProtocolConfig config = rpcClientConfig.getRpcProtocolConfig();
        map.from(properties::getProtocolVersion)
                .whenNonNull()
                .to(config::setProtocolVersion);
        map.from(properties::getSerializer)
                .whenNonNull()
                .to(config::setSerializer);
        map.from(properties::getCompressor)
                .whenNonNull()
                .to(config::setCompressor);
        map.from(properties::getSequenceIdGenerator)
                .whenNonNull()
                .to(config::setSequenceIdGenerator);
    }
}
