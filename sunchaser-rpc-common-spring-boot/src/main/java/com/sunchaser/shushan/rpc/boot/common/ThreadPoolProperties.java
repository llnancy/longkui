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
