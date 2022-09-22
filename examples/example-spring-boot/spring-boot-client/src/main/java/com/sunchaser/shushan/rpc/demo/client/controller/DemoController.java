package com.sunchaser.shushan.rpc.demo.client.controller;

import com.sunchaser.shushan.rpc.boot.client.annotation.RpcReference;
import com.sunchaser.shushan.rpc.core.call.CallType;
import com.sunchaser.shushan.rpc.core.call.RpcCallback;
import com.sunchaser.shushan.rpc.core.call.RpcCallbackHolder;
import com.sunchaser.shushan.rpc.core.call.RpcFutureHolder;
import com.sunchaser.shushan.rpc.core.util.ThrowableUtils;
import com.sunchaser.shushan.rpc.demo.facade.HelloWorldService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Future;

/**
 * demo controller
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/22
 */
@RestController
@Slf4j
public class DemoController {

    @RpcReference(group = "hello", version = "0.0.1")
    private HelloWorldService helloWorldService;

    @RpcReference(group = "hello", version = "0.0.2")
    private HelloWorldService helloWorldServiceV2;

    @RpcReference(group = "hello", version = "0.0.1", callType = CallType.FUTURE)
    private HelloWorldService futureService;

    @RpcReference(group = "hello", version = "0.0.1", callType = CallType.CALLBACK)
    private HelloWorldService callbackService;

    @RpcReference(group = "hello", version = "0.0.1", callType = CallType.ONEWAY)
    private HelloWorldService onewayService;

    @GetMapping("/hello")
    public String hello(String hello) {
        return helloWorldService.hello(hello);
    }

    @GetMapping("/world")
    public String world(String wor, Integer l, Long d) {
        return helloWorldServiceV2.world(wor, l, d);
    }

    @GetMapping("/future")
    public String future(String hello) throws Exception {
        futureService.hello(hello);
        Future<String> future = RpcFutureHolder.getFuture();
        return future.get();
    }

    @GetMapping("/callback")
    public void callback(String hello) {
        RpcCallbackHolder.setCallback(new RpcCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LOGGER.info("callback onSuccess. result:{}", result);
            }

            @Override
            public void onError(Throwable t) {
                ThrowableUtils.toString(t);
            }
        });
        callbackService.hello(hello);
    }

    @GetMapping("oneway")
    public void oneway(String hello) {
        onewayService.hello(hello);
    }
}
