package com.sunchaser.shushan.rpc.demo.server.service.impl;

import com.sunchaser.shushan.rpc.demo.facade.HelloWorldService;
import com.sunchaser.shushan.rpc.boot.server.annotation.RpcService;
import lombok.extern.slf4j.Slf4j;

/**
 * HelloWorldService implementation v1
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/21
 */
@RpcService(version = "0.0.1", group = "hello")
@Slf4j
public class HelloWorldServiceImpl implements HelloWorldService {

    @Override
    public String hello(String hello) {
        LOGGER.info("hello world (hello) :{}", hello);
        return "hello world (hello) :" + hello;
    }

    @Override
    public String world(String wor, Integer l, Long d) {
        LOGGER.info("hello world (world) :{}, {}, {}", wor, l, d);
        return "hello world (world) :" + wor + ", " + l + ", " + d;
    }
}
