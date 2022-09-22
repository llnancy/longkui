package com.sunchaser.shushan.rpc.demo.server.service.impl;

import com.sunchaser.shushan.rpc.demo.facade.HelloWorldService;

/**
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/21
 */
public class HelloWorldV2ServiceImpl implements HelloWorldService {

    @Override
    public String hello(String hello) {
        return "hello world v2 (hello) :" + hello;
    }

    @Override
    public String world(String wor, Integer l, Long d) {
        return "hello world v2 (world) :" + wor + ", " + l + ", " + d;
    }
}
