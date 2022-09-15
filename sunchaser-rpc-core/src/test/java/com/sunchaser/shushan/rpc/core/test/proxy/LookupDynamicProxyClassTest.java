package com.sunchaser.shushan.rpc.core.test.proxy;

import com.sunchaser.shushan.rpc.core.config.RpcServiceConfig;
import com.sunchaser.shushan.rpc.core.proxy.impl.ByteBuddyRpcDynamicProxy;
import com.sunchaser.shushan.rpc.core.proxy.impl.CglibRpcDynamicProxy;
import com.sunchaser.shushan.rpc.core.proxy.impl.JavassistRpcDynamicProxy;
import com.sunchaser.shushan.rpc.core.proxy.impl.JdkRpcDynamicProxy;
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
        HelloService helloService = JdkRpcDynamicProxy.getInstance().createProxyInstance(rpcServiceConfig);
        HelloService cglib = CglibRpcDynamicProxy.getInstance().createProxyInstance(rpcServiceConfig);
        HelloService javassist = JavassistRpcDynamicProxy.getInstance().createProxyInstance(rpcServiceConfig);
        HelloService byteBuddy = ByteBuddyRpcDynamicProxy.getInstance().createProxyInstance(rpcServiceConfig);
        System.in.read();
    }
}
