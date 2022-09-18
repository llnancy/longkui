package com.sunchaser.shushan.rpc.core.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * rpc framework config
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RpcApplicationConfig {

    private RpcProtocolConfig rpcProtocolConfig = RpcProtocolConfig.createDefaultConfig();

    private RpcServerConfig rpcServerConfig = RpcServerConfig.createDefaultConfig();

    private RpcServiceConfig rpcServiceConfig = RpcServiceConfig.createDefaultConfig();

    private RpcFrameworkConfig rpcFrameworkConfig = RpcFrameworkConfig.createDefaultConfig();

    public static RpcApplicationConfig createDefaultConfig(Class<?> clazz) {
        RpcApplicationConfig rpcApplicationConfig = new RpcApplicationConfig();
        RpcServiceConfig rpcServiceConfig = rpcApplicationConfig.getRpcServiceConfig();
        rpcServiceConfig.setTargetClass(clazz);
        return rpcApplicationConfig;
    }
}
