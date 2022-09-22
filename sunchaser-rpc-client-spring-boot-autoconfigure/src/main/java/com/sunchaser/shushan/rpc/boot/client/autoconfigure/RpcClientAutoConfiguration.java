package com.sunchaser.shushan.rpc.boot.client.autoconfigure;

import com.sunchaser.shushan.rpc.boot.client.support.RpcReferenceBeanPostProcessor;
import com.sunchaser.shushan.rpc.core.config.RpcClientConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * rpc client auto configuration
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/21
 */
@Configuration
@EnableConfigurationProperties({RpcClientProperties.class})
public class RpcClientAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(RpcReferenceBeanPostProcessor.class)
    public RpcReferenceBeanPostProcessor rpcReferenceBeanPostProcessor(RpcClientProperties properties) {
        RpcClientConfig rpcClientConfig = RpcClientConfig.createDefaultConfig();
        new RpcClientConfigurer(properties).configure(rpcClientConfig);
        return new RpcReferenceBeanPostProcessor(rpcClientConfig);
    }
}
