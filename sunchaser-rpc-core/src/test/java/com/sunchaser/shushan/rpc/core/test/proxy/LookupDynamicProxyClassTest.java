package com.sunchaser.shushan.rpc.core.test.proxy;

import com.sunchaser.shushan.rpc.core.config.RpcServiceConfig;
import com.sunchaser.shushan.rpc.core.proxy.RpcDynamicProxyFactory;
import com.sunchaser.shushan.rpc.core.test.HelloService;

/**
 * alibaba arthas查看动态代理生成的class
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/8/24
 */
public class LookupDynamicProxyClassTest {

    public static void main(String[] args) throws Exception {
        RpcServiceConfig rpcServiceConfig = RpcServiceConfig.createDefaultConfig(HelloService.class);
        HelloService helloService = RpcDynamicProxyFactory.getRpcProxyInstance("jdk", rpcServiceConfig);
        HelloService cglib = RpcDynamicProxyFactory.getRpcProxyInstance("cglib", rpcServiceConfig);
        HelloService javassist = RpcDynamicProxyFactory.getRpcProxyInstance("javassist", rpcServiceConfig);
        HelloService byteBuddy = RpcDynamicProxyFactory.getRpcProxyInstance("byteBuddy", rpcServiceConfig);
        System.in.read();
    }
}
