package com.sunchaser.shushan.rpc.server.autoconfigure;

import com.sunchaser.shushan.rpc.core.common.Constants;
import com.sunchaser.shushan.rpc.core.config.RpcServerConfig;
import com.sunchaser.shushan.rpc.core.extension.ExtensionLoader;
import com.sunchaser.shushan.rpc.core.transport.server.NettyRpcServer;
import com.sunchaser.shushan.rpc.core.transport.server.RpcServer;
import com.sunchaser.shushan.rpc.server.support.RpcServiceBeanPostProcessor;
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
    @ConditionalOnMissingBean(RpcServer.class)
    public RpcServer rpcServer(RpcServerProperties properties) {
        RpcServerConfig config = properties.getConfig();
        String rpcServer = config.getRpcServer();
        if (!Constants.NETTY.equals(rpcServer)) {
            return ExtensionLoader.getExtensionLoader(RpcServer.class).getExtension(rpcServer);
        }
        return new NettyRpcServer(config);
    }

    @Bean
    @ConditionalOnMissingBean(RpcServiceBeanPostProcessor.class)
    public RpcServiceBeanPostProcessor rpcServiceBeanPostProcessor(RpcServerProperties properties) {
        return new RpcServiceBeanPostProcessor(properties);
    }
}
