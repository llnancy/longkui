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

package com.sunchaser.shushan.rpc.boot.common;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.TimeUnit;

/**
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/22
 */
@Getter
@Setter
public class ThreadPoolProperties {

    /**
     * thread name identifier
     */
    private String threadNameIdentifier;

    /**
     * core pool size
     */
    private Integer corePoolSize;

    /**
     * max pool size
     */
    private Integer maximumPoolSize;

    /**
     * keepalive time
     */
    private Long keepAliveTime;

    /**
     * time unit
     */
    private TimeUnit unit;

    /**
     * work queue capacity
     */
    private Integer workQueueCapacity;

    /**
     * work queue type
     */
    private WorkQueueType workQueueType;
}
