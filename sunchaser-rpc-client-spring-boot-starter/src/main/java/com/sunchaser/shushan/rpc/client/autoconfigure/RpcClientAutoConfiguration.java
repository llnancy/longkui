package com.sunchaser.shushan.rpc.client.autoconfigure;

import com.sunchaser.shushan.rpc.client.support.RpcReferenceBeanPostProcessor;
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
        return new RpcReferenceBeanPostProcessor(properties);
    }
}
