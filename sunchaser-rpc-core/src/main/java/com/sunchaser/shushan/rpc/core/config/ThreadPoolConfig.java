package com.sunchaser.shushan.rpc.core.config;

import com.google.common.collect.Queues;
import com.sunchaser.shushan.rpc.core.common.Constants;
import lombok.*;

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
