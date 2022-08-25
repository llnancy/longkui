package com.sunchaser.shushan.rpc.core.test.proxy;

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
        HelloService helloService = RpcDynamicProxyFactory.getRpcProxyInstance("jdk", HelloService.class);
        HelloService cglib = RpcDynamicProxyFactory.getRpcProxyInstance("cglib", HelloService.class);
        HelloService javassist = RpcDynamicProxyFactory.getRpcProxyInstance("javassist", HelloService.class);
        HelloService byteBuddy = RpcDynamicProxyFactory.getRpcProxyInstance("byteBuddy", HelloService.class);
        System.in.read();
    }
}
