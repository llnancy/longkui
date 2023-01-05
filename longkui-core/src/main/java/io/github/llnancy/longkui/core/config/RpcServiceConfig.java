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

package io.github.llnancy.longkui.core.config;

import io.github.llnancy.longkui.core.balancer.Weightable;
import io.github.llnancy.longkui.core.call.CallType;
import io.github.llnancy.longkui.core.common.Constants;
import io.github.llnancy.longkui.core.util.ServiceUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * rpc service config
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class RpcServiceConfig {

    /**
     * 默认版本号
     */
    public static final String DEFAULT_VERSION = "0.0.0";

    /**
     * 默认分组
     */
    public static final String DEFAULT_GROUP = Constants.DEFAULT;

    /**
     * 默认超时时间
     */
    public static final long DEFAULT_TIMEOUT = 0L;

    /**
     * rpc服务提供类Class（目标代理类的Class）
     */
    private Class<?> targetClass;

    /**
     * rpc服务版本号
     */
    private String version = DEFAULT_VERSION;

    /**
     * rpc服务分组
     */
    private String group = DEFAULT_GROUP;

    /**
     * rpc service node weight
     */
    private Integer weight = Weightable.DEFAULT_WEIGHT;

    /**
     * rpc service node warmup time
     */
    private Integer warmup = Weightable.DEFAULT_WARMUP;

    /**
     * rpc调用超时时间
     */
    private long timeout = DEFAULT_TIMEOUT;

    /**
     * rpc call type, default SYNC
     */
    private CallType callType = CallType.SYNC;

    public String getClassName() {
        return this.targetClass.getName();
    }

    public String getRpcServiceKey() {
        return ServiceUtils.buildServiceKey(this.getClassName(), this.group, this.version);
    }

    public static <T> RpcServiceConfig createDefaultConfig(Class<T> clazz) {
        return new RpcServiceConfig().setTargetClass(clazz);
    }
}
