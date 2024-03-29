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

package io.github.llnancy.longkui.core.provider;

/**
 * service provider
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/18
 */
public interface ServiceProvider {

    /**
     * 注册服务提供者
     *
     * @param serviceKey service key
     * @param service    service object
     */
    void registerProvider(String serviceKey, Object service);

    /**
     * 获取服务提供者
     *
     * @param serviceKey service key
     * @return service object
     */
    Object getProvider(String serviceKey);
}
