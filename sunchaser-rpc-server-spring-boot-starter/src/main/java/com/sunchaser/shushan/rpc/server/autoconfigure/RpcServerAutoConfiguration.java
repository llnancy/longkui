package com.sunchaser.shushan.rpc.server.autoconfigure;

import com.sunchaser.shushan.rpc.core.config.RpcServerConfig;
import com.sunchaser.shushan.rpc.server.support.RpcServerStarter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * rpc server auto configuration
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/21
 */
@Configuration
@EnableConfigurationProperties({RpcServerProperties.class})
public class RpcServerAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(RpcServerStarter.class)
    public RpcServerStarter rpcServiceBeanPostProcessor(RpcServerProperties properties) {
        RpcServerConfig rpcServerConfig = RpcServerConfig.createDefaultConfig();
        new RpcServerConfigurer(properties).configure(rpcServerConfig);
        return new RpcServerStarter(rpcServerConfig);
    }
}
