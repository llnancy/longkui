package com.sunchaser.shushan.rpc.core.test.proxy;

import com.sunchaser.shushan.rpc.core.config.RpcClientConfig;
import com.sunchaser.shushan.rpc.core.proxy.impl.ByteBuddyDynamicProxy;
import com.sunchaser.shushan.rpc.core.proxy.impl.CglibDynamicProxy;
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
        RpcClientConfig rpcClientConfig = RpcClientConfig.createDefaultConfig(HelloService.class);
        HelloService helloService = JdkDynamicProxy.getInstance().createProxyInstance(rpcClientConfig);
        HelloService cglib = CglibDynamicProxy.getInstance().createProxyInstance(rpcClientConfig);
        HelloService javassist = JavassistDynamicProxy.getInstance().createProxyInstance(rpcClientConfig);
        HelloService byteBuddy = ByteBuddyDynamicProxy.getInstance().createProxyInstance(rpcClientConfig);
        System.in.read();
    }
}
