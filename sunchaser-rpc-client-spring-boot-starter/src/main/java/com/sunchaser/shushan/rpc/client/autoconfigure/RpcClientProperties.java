package com.sunchaser.shushan.rpc.client.autoconfigure;

import com.sunchaser.shushan.rpc.core.config.RpcClientConfig;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * rpc client properties
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/21
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "sunchaser.rpc.client")
public class RpcClientProperties {

    /**
     * rpc client config
     */
    @NestedConfigurationProperty
    private RpcClientConfig config;
}
