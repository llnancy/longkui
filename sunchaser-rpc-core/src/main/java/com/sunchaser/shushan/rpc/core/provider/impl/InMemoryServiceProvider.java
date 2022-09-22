/*
 * Copyright 2022 SunChaser
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

package com.sunchaser.shushan.rpc.core.provider.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.sunchaser.shushan.rpc.core.exceptions.RpcException;
import com.sunchaser.shushan.rpc.core.provider.ServiceProvider;
import lombok.SneakyThrows;

import java.util.Map;

/**
 * a service provider implementation based on memory
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/18
 */
public class InMemoryServiceProvider implements ServiceProvider {

    private static final Map<String, Object> SERVICE_PROVIDER_MAP = Maps.newConcurrentMap();

    private static final ServiceProvider INSTANCE = new InMemoryServiceProvider();

    public static ServiceProvider getInstance() {
        return INSTANCE;
    }

    /**
     * 注册服务提供者实例
     *
     * @param serviceKey service key
     * @param service    service object
     */
    @SneakyThrows
    @Override
    public void registerProvider(String serviceKey, Object service) {
        SERVICE_PROVIDER_MAP.put(serviceKey, service);
    }

    /**
     * 获取服务提供者实例
     *
     * @param serviceKey service key
     * @return service object
     * @throws RpcException throws on service be null
     */
    @Override
    public Object getProvider(String serviceKey) {
        Object service = SERVICE_PROVIDER_MAP.get(serviceKey);
        Preconditions.checkNotNull(service, serviceKey + ". service does not exist.");
        return service;
    }
}
