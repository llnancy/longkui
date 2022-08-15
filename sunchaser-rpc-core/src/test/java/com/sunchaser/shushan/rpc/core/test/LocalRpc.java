package com.sunchaser.shushan.rpc.core.test;

import com.sunchaser.shushan.rpc.core.proxy.RpcProxyFactory;
import com.sunchaser.shushan.rpc.core.registry.Registry;
import com.sunchaser.shushan.rpc.core.registry.ServiceMeta;
import com.sunchaser.shushan.rpc.core.registry.impl.LocalRegistry;
import com.sunchaser.shushan.rpc.core.transport.RpcServer;
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
        ServiceMeta serviceMeta = ServiceMeta.builder()
                .serviceName(HelloService.class.getName())
                .version("1")
                .address("127.0.0.1")
                .port(1234)
                .build();
        Registry registry = LocalRegistry.getInstance();
        registry.register(serviceMeta);
        new RpcServer().start();

        // consumer
        HelloService helloService = RpcProxyFactory.getRpcProxyInstance(HelloService.class, registry);
        String hello = helloService.sayHi("SunChaser", null, 1L);
        LOGGER.info("sayHello result: {}", hello);
    }
}
