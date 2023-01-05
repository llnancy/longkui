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

import com.google.common.collect.Queues;
import io.github.llnancy.longkui.core.common.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * ThreadPool Config
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class ThreadPoolConfig {

    /**
     * default thread name identifier: default
     */
    public static final String DEFAULT_THREAD_NAME_IDENTIFIER = Constants.DEFAULT;

    /**
     * default core pool size: 10
     */
    public static final Integer DEFAULT_CORE_POOL_SIZE = 10;

    /**
     * default max pool size: 100
     */
    public static final Integer DEFAULT_MAXIMUM_POOL_SIZE_SIZE = 100;

    /**
     * default keepalive time: 60
     */
    public static final Long DEFAULT_KEEP_ALIVE_TIME = 60L;

    /**
     * default keepalive time unit: seconds
     */
    public static final TimeUnit DEFAULT_UNIT = TimeUnit.SECONDS;

    /**
     * default work queue capacity: 1000
     */
    public static final Integer DEFAULT_WORK_QUEUE_CAPACITY = 1000;

    /**
     * thread name identifier
     */
    private String threadNameIdentifier = DEFAULT_THREAD_NAME_IDENTIFIER;

    /**
     * core pool size
     */
    private Integer corePoolSize = DEFAULT_CORE_POOL_SIZE;

    /**
     * max pool size
     */
    private Integer maximumPoolSize = DEFAULT_MAXIMUM_POOL_SIZE_SIZE;

    /**
     * keepalive time
     */
    private Long keepAliveTime = DEFAULT_KEEP_ALIVE_TIME;

    /**
     * time unit
     */
    private TimeUnit unit = DEFAULT_UNIT;

    /**
     * work queue capacity
     */
    private Integer workQueueCapacity = DEFAULT_WORK_QUEUE_CAPACITY;

    /**
     * work queue
     */
    private BlockingQueue<Runnable> workQueue = Queues.newLinkedBlockingQueue(workQueueCapacity);

    /**
     * create default config
     *
     * @return default ThreadPoolConfig
     */
    public static ThreadPoolConfig createDefaultConfig() {
        return new ThreadPoolConfig();
    }
}
