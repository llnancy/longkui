package com.sunchaser.shushan.rpc.core.test;

import com.sunchaser.shushan.rpc.core.config.RpcApplicationConfig;
import com.sunchaser.shushan.rpc.core.config.RpcServerConfig;
import com.sunchaser.shushan.rpc.core.config.RpcServiceConfig;
import com.sunchaser.shushan.rpc.core.provider.ServiceProvider;
import com.sunchaser.shushan.rpc.core.provider.impl.InMemoryServiceProvider;
import com.sunchaser.shushan.rpc.core.proxy.DynamicProxy;
import com.sunchaser.shushan.rpc.core.proxy.impl.JdkDynamicProxy;
import com.sunchaser.shushan.rpc.core.registry.Registry;
import com.sunchaser.shushan.rpc.core.registry.ServiceMetaData;
import com.sunchaser.shushan.rpc.core.registry.impl.LocalRegistry;
import com.sunchaser.shushan.rpc.core.transport.server.NettyRpcServer;
import lombok.extern.slf4j.Slf4j;

/**
 * use LocalRegistry
 * Must be in a JVM process
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/17
 */
@Slf4j
public class LocalRpc {

    public static void main(String[] args) {
        // provider
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
        Registry registry = LocalRegistry.getInstance();
        registry.register(serviceMetaData);

        // rpc server
        new NettyRpcServer(rpcServerConfig).start();

        // consumer
        DynamicProxy dynamicProxy = JdkDynamicProxy.getInstance();
        HelloService helloService = dynamicProxy.createProxyInstance(rpcApplicationConfig);
        String hello = helloService.sayHello("SunChaser", null, 1L);
        LOGGER.info("sayHello result: {}", hello);
    }
}
