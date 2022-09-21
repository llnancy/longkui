package com.sunchaser.shushan.rpc.server.autoconfigure;

import com.sunchaser.shushan.rpc.core.config.RpcServerConfig;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * rpc server properties
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/21
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "sunchaser.rpc.server")
public class RpcServerProperties {

    /**
     * rpc server config
     */
    @NestedConfigurationProperty
    private RpcServerConfig config;
}
