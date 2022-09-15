package com.sunchaser.shushan.rpc.core.test;

import com.sunchaser.shushan.rpc.core.common.Constants;
import com.sunchaser.shushan.rpc.core.config.RpcServiceConfig;
import com.sunchaser.shushan.rpc.core.extension.ExtensionLoader;
import com.sunchaser.shushan.rpc.core.registry.Registry;
import com.sunchaser.shushan.rpc.core.registry.RegistryEnum;
import com.sunchaser.shushan.rpc.core.registry.ServiceMetaData;
import com.sunchaser.shushan.rpc.core.transport.server.RpcServer;
import com.sunchaser.shushan.rpc.core.util.BeanFactory;

/**
 * rpc provider
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/17
 */
public class Provider {

    public static void main(String[] args) throws Exception {
        BeanFactory.register(HelloService.class.getName(), new HelloServiceImpl());
        RpcServiceConfig rpcServiceConfig = RpcServiceConfig.createDefaultConfig(HelloService.class);
        ServiceMetaData serviceMetaData = ServiceMetaData.builder()
                .serviceKey(rpcServiceConfig.getRpcServiceKey())
                .host("127.0.0.1")
                .port(1234)
                .build();
        Registry registry = ExtensionLoader.getExtensionLoader(Registry.class).getExtension(RegistryEnum.ZOOKEEPER);
        registry.register(serviceMetaData);
        RpcServer rpcServer = ExtensionLoader.getExtensionLoader(RpcServer.class).getExtension(Constants.NETTY);
        rpcServer.start();
        System.in.read();
    }
}
