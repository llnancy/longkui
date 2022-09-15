package com.sunchaser.shushan.rpc.core.test.proxy;

import com.sunchaser.shushan.rpc.core.config.RpcFrameworkConfig;
import com.sunchaser.shushan.rpc.core.config.RpcServiceConfig;
import com.sunchaser.shushan.rpc.core.proxy.impl.*;
import com.sunchaser.shushan.rpc.core.proxy.impl.JavassistDynamicProxy;
import com.sunchaser.shushan.rpc.core.proxy.impl.JdkDynamicProxy;
import com.sunchaser.shushan.rpc.core.test.HelloService;

/**
 * alibaba arthas查看动态代理生成的class
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/8/24
 */
public class LookupDynamicProxyClassTest {

    public static void main(String[] args) throws Exception {
        RpcFrameworkConfig rpcFrameworkConfig = RpcFrameworkConfig.createDefaultConfig(HelloService.class);
        HelloService helloService = JdkDynamicProxy.getInstance().createProxyInstance(rpcFrameworkConfig);
        HelloService cglib = CglibDynamicProxy.getInstance().createProxyInstance(rpcFrameworkConfig);
        HelloService javassist = JavassistDynamicProxy.getInstance().createProxyInstance(rpcFrameworkConfig);
        HelloService byteBuddy = ByteBuddyDynamicProxy.getInstance().createProxyInstance(rpcFrameworkConfig);
        System.in.read();
    }
}
