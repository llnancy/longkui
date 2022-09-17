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
public class RpcFrameworkConfig {

    private RpcProtocolConfig rpcProtocolConfig = RpcProtocolConfig.createDefaultConfig();

    private RpcServerConfig rpcServerConfig = RpcServerConfig.createDefaultConfig();

    private RpcServiceConfig rpcServiceConfig = RpcServiceConfig.createDefaultConfig();

    private RpcComponentConfig rpcComponentConfig = RpcComponentConfig.createDefaultConfig();

    public static RpcFrameworkConfig createDefaultConfig(Class<?> clazz) {
        RpcFrameworkConfig rpcFrameworkConfig = new RpcFrameworkConfig();
        RpcServiceConfig rpcServiceConfig = rpcFrameworkConfig.getRpcServiceConfig();
        rpcServiceConfig.setTargetClass(clazz);
        return rpcFrameworkConfig;
    }
}
