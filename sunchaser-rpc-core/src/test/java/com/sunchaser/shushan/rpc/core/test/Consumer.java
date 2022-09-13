package com.sunchaser.shushan.rpc.core.test;

import com.sunchaser.shushan.rpc.core.config.RpcServiceConfig;
import com.sunchaser.shushan.rpc.core.proxy.RpcDynamicProxyFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;

/**
 * rpc consumer
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/17
 */
@Slf4j
public class Consumer {

    public static void main(String[] args) throws Exception {
        RpcServiceConfig rpcServiceConfig = RpcServiceConfig.createDefaultConfig(HelloService.class);
        HelloService helloService = RpcDynamicProxyFactory.getRpcProxyInstance(rpcServiceConfig);
        String hello = helloService.sayHello("SunChaser");
        LOGGER.info("sayHello result: {}", hello);
        Assertions.assertEquals("Hello:" + "SunChaser", hello);
        System.in.read();
    }
}
