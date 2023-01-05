/*
 * Copyright 2022 LongKui
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

package io.github.llnancy.longkui.demo.server.service.impl;

import io.github.llnancy.longkui.boot.server.annotation.RpcService;
import io.github.llnancy.longkui.demo.facade.HelloWorldService;
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
