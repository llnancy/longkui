package com.sunchaser.shushan.rpc.core.test;

import com.sunchaser.shushan.rpc.core.common.Constants;
import com.sunchaser.shushan.rpc.core.config.RpcApplicationConfig;
import com.sunchaser.shushan.rpc.core.config.RpcServerConfig;
import com.sunchaser.shushan.rpc.core.config.RpcServiceConfig;
import com.sunchaser.shushan.rpc.core.extension.ExtensionLoader;
import com.sunchaser.shushan.rpc.core.provider.ServiceProvider;
import com.sunchaser.shushan.rpc.core.provider.impl.InMemoryServiceProvider;
import com.sunchaser.shushan.rpc.core.registry.Registry;
import com.sunchaser.shushan.rpc.core.registry.RegistryEnum;
import com.sunchaser.shushan.rpc.core.registry.ServiceMetaData;
import com.sunchaser.shushan.rpc.core.transport.server.RpcServer;

/**
 * rpc provider
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/17
 */
public class Provider {

    public static void main(String[] args) throws Exception {
        RpcApplicationConfig rpcApplicationConfig = RpcApplicationConfig.createDefaultConfig(HelloService.class);
        RpcServiceConfig rpcServiceConfig = rpcApplicationConfig.getRpcServiceConfig();
        RpcServerConfig rpcServerConfig = rpcApplicationConfig.getRpcServerConfig();
        String serviceKey = rpcServiceConfig.getRpcServiceKey();

        // service provider
        ServiceProvider serviceProvider = InMemoryServiceProvider.getInstance();
        serviceProvider.registerProvider(serviceKey, new HelloServiceImpl());

        // registry
        ServiceMetaData serviceMetaData = ServiceMetaData.builder()
                .serviceKey(serviceKey)
                .host(rpcServerConfig.getHost())
                .port(rpcServerConfig.getPort())
                .build();
        Registry registry = ExtensionLoader.getExtensionLoader(Registry.class).getExtension(RegistryEnum.ZOOKEEPER);
        registry.register(serviceMetaData);

        // rpc server
        RpcServer rpcServer = ExtensionLoader.getExtensionLoader(RpcServer.class).getExtension(Constants.NETTY);
        rpcServer.start();
        System.in.read();
    }
}
