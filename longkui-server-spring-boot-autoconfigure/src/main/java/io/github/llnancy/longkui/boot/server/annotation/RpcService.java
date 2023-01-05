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

package io.github.llnancy.longkui.boot.server.annotation;

import io.github.llnancy.longkui.core.balancer.Weightable;
import io.github.llnancy.longkui.core.config.RpcServiceConfig;
import org.springframework.stereotype.Service;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * rpc service annotation
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/21
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Service
public @interface RpcService {

    String group() default RpcServiceConfig.DEFAULT_GROUP;

    String version() default RpcServiceConfig.DEFAULT_VERSION;

    int weight() default Weightable.DEFAULT_WEIGHT;

    int warmup() default Weightable.DEFAULT_WARMUP;
}
