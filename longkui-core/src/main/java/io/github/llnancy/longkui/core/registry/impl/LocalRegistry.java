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

package io.github.llnancy.longkui.core.registry.impl;

import com.google.common.collect.Maps;
import io.github.llnancy.longkui.core.registry.Registry;
import io.github.llnancy.longkui.core.registry.ServiceMetaData;

import java.util.concurrent.ConcurrentMap;

/**
 * Local registry
 * Must be in a JVM process
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/17
 */
public final class LocalRegistry implements Registry {

    private final ConcurrentMap<String, ServiceMetaData> services = Maps.newConcurrentMap();

    private LocalRegistry() {
    }

    private static final Registry INSTANCE = new LocalRegistry();

    public static Registry getInstance() {
        return INSTANCE;
    }

    @Override
    public void register(ServiceMetaData serviceMetaData) {
        services.put(serviceMetaData.getServiceKey(), serviceMetaData);
    }

    @Override
    public void unRegister(ServiceMetaData serviceMetaData) {
        services.remove(serviceMetaData.getServiceKey());
    }

    @Override
    public ServiceMetaData discovery(String serviceKey) {
        return services.get(serviceKey);
    }

    @Override
    public void destroy() {
        services.clear();
    }
}
