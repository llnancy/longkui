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

package io.github.llnancy.longkui.core.registry;

import io.github.llnancy.longkui.core.extension.SPI;

/**
 * 服务注册与发现接口
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/14
 */
@SPI
public interface Registry {

    /**
     * 服务注册
     *
     * @param serviceMetaData ServiceMetaData
     */
    void register(ServiceMetaData serviceMetaData);

    /**
     * 服务注销
     *
     * @param serviceMetaData ServiceMetaData
     */
    void unRegister(ServiceMetaData serviceMetaData);

    /**
     * 服务发现
     *
     * @param serviceKey serviceKey
     * @return ServiceMetaData
     */
    ServiceMetaData discovery(String serviceKey);

    /**
     * 注册中心销毁
     */
    void destroy();
}
