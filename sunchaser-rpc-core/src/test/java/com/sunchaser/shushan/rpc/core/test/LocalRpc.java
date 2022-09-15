package com.sunchaser.shushan.rpc.core.test;

import com.sunchaser.shushan.rpc.core.config.RpcFrameworkConfig;
import com.sunchaser.shushan.rpc.core.config.RpcServiceConfig;
import com.sunchaser.shushan.rpc.core.proxy.DynamicProxy;
import com.sunchaser.shushan.rpc.core.proxy.impl.JdkDynamicProxy;
import com.sunchaser.shushan.rpc.core.registry.Registry;
import com.sunchaser.shushan.rpc.core.registry.ServiceMetaData;
import com.sunchaser.shushan.rpc.core.registry.impl.LocalRegistry;
import com.sunchaser.shushan.rpc.core.transport.server.NettyRpcServer;
import com.sunchaser.shushan.rpc.core.util.BeanFactory;
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
        BeanFactory.register(HelloService.class.getName(), new HelloServiceImpl());
        RpcFrameworkConfig rpcFrameworkConfig = RpcFrameworkConfig.createDefaultConfig(HelloService.class);
        RpcServiceConfig rpcServiceConfig = rpcFrameworkConfig.getRpcServiceConfig();
        ServiceMetaData serviceMetaData = ServiceMetaData.builder()
                .serviceKey(rpcServiceConfig.getRpcServiceKey())
                .host("127.0.0.1")
                .port(1234)
                .build();
        Registry registry = LocalRegistry.getInstance();
        registry.register(serviceMetaData);
        new NettyRpcServer().start();

        // consumer
        DynamicProxy dynamicProxy = JdkDynamicProxy.getInstance();
        HelloService helloService = dynamicProxy.createProxyInstance(rpcFrameworkConfig);
        String hello = helloService.sayHello("SunChaser", null, 1L);
        LOGGER.info("sayHello result: {}", hello);
    }
}
