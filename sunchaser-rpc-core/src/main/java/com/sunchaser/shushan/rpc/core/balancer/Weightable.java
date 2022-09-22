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

package com.sunchaser.shushan.rpc.core.balancer;

/**
 * 权重
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/8/31
 */
public interface Weightable {

    /**
     * 默认权重值：1
     */
    int DEFAULT_WEIGHT = 1;

    /**
     * 默认预热时间：10分钟
     */
    int DEFAULT_WARMUP = 10 * 60 * 1000;

    /**
     * 获取权重
     *
     * @return 权重值
     */
    default int getWeight() {
        return DEFAULT_WEIGHT;
    }

    /**
     * 获取预热时间
     *
     * @return 预热时间
     */
    default int getWarmup() {
        return DEFAULT_WARMUP;
    }
}
