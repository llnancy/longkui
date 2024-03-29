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

import io.github.llnancy.longkui.demo.facade.HelloWorldService;

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
