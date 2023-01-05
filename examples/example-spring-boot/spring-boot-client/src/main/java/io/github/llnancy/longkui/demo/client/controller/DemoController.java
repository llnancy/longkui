/*
 * Copyright 2023 LongKui
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Copyright 2022 LongKui
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.llnancy.longkui.demo.client.controller;

import io.github.llnancy.longkui.boot.client.annotation.RpcReference;
import io.github.llnancy.longkui.core.call.CallType;
import io.github.llnancy.longkui.core.call.RpcCallback;
import io.github.llnancy.longkui.core.call.RpcCallbackHolder;
import io.github.llnancy.longkui.core.call.RpcFutureHolder;
import io.github.llnancy.longkui.core.util.ThrowableUtils;
import io.github.llnancy.longkui.demo.facade.HelloWorldService;
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

    /**
     * callback
     *
     * @param hello hello
     */
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
