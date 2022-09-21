package com.sunchaser.shushan.rpc.demo.server.service.impl;

import com.sunchaser.shushan.rpc.demo.facade.HelloWorldService;
import com.sunchaser.shushan.rpc.server.annotation.RpcService;

/**
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/21
 */
@RpcService(version = "0.0.1", group = "hello")
public class HelloWorldServiceImpl implements HelloWorldService {

    @Override
    public String hello(String hello) {
        return "hello world (hello) :" + hello;
    }

    @Override
    public String world(String wor, Integer l, Long d) {
        return "hello world (world) :" + wor + ", " + l + ", " + d;
    }
}
