package com.sunchaser.shushan.rpc.core.config;

import com.google.common.collect.Queues;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
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
public class ThreadPoolConfig {

    private static final Integer DEFAULT_CORE_POOL_SIZE = 10;

    private static final Integer DEFAULT_MAXIMUM_POOL_SIZE_SIZE = 100;

    private static final Long DEFAULT_KEEP_ALIVE_TIME = 60L;

    private static final TimeUnit DEFAULT_UNIT = TimeUnit.SECONDS;

    private static final Integer DEFAULT_WORK_QUEUE_CAPACITY = 1000;

    private Integer corePoolSize = DEFAULT_CORE_POOL_SIZE;

    private Integer maximumPoolSize = DEFAULT_MAXIMUM_POOL_SIZE_SIZE;

    private Long keepAliveTime = DEFAULT_KEEP_ALIVE_TIME;

    private TimeUnit unit = DEFAULT_UNIT;

    private Integer workQueueCapacity = DEFAULT_WORK_QUEUE_CAPACITY;

    private BlockingQueue<Runnable> workQueue = Queues.newLinkedBlockingQueue(workQueueCapacity);

    public static ThreadPoolConfig createDefaultConfig() {
        return new ThreadPoolConfig();
    }
}
