package com.sunchaser.shushan.rpc.core.test;

import com.sunchaser.shushan.rpc.core.proxy.RpcDynamicProxyFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * rpc consumer
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/17
 */
@Slf4j
public class Consumer {

    public static void main(String[] args) {
        HelloService helloService = RpcDynamicProxyFactory.getRpcProxyInstance(HelloService.class);
        String hello = helloService.sayHello("SunChaser");
        LOGGER.info("sayHello result: {}", hello);
    }
}
