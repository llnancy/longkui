package com.sunchaser.rpc.core.test;

import com.sunchaser.rpc.core.proxy.RpcProxy;
import com.sunchaser.rpc.core.registry.Registry;
import com.sunchaser.rpc.core.registry.impl.ZookeeperRegistry;
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
        Registry registry = new ZookeeperRegistry();
        HelloService helloService = RpcProxy.newInstance(HelloService.class, registry);
        String hello = helloService.sayHello("SunChaser");
        log.info("sayHello result: {}", hello);
    }
}
