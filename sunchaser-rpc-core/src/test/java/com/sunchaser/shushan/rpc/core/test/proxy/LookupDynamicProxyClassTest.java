package com.sunchaser.shushan.rpc.core.test.proxy;

import com.sunchaser.shushan.rpc.core.config.RpcApplicationConfig;
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
        RpcApplicationConfig rpcApplicationConfig = RpcApplicationConfig.createDefaultConfig(HelloService.class);
        HelloService helloService = JdkDynamicProxy.getInstance().createProxyInstance(rpcApplicationConfig);
        HelloService cglib = CglibDynamicProxy.getInstance().createProxyInstance(rpcApplicationConfig);
        HelloService javassist = JavassistDynamicProxy.getInstance().createProxyInstance(rpcApplicationConfig);
        HelloService byteBuddy = ByteBuddyDynamicProxy.getInstance().createProxyInstance(rpcApplicationConfig);
        System.in.read();
    }
}
