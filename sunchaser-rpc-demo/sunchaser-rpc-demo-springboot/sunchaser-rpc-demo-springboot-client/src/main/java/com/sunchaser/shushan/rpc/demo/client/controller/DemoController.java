package com.sunchaser.shushan.rpc.demo.client.controller;

import com.sunchaser.shushan.rpc.client.annotation.RpcReference;
import com.sunchaser.shushan.rpc.demo.facade.HelloWorldService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/22
 */
@RestController
public class DemoController {

    @RpcReference(group = "hello", version = "0.0.1")
    private HelloWorldService helloWorldService;

    @RpcReference(group = "hello", version = "0.0.2")
    private HelloWorldService helloWorldServiceV2;

    @GetMapping("/hello")
    public String hello(String hello) {
        return helloWorldService.hello(hello);
    }

    @GetMapping("/world")
    public String world(String wor, Integer l, Long d) {
        return helloWorldServiceV2.world(wor, l, d);
    }
}
