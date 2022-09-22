package com.sunchaser.shushan.rpc.boot.common;

import com.google.common.collect.Queues;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;

/**
 * work queue type enum
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/22
 */
public enum WorkQueueType {

    /**
     * {@link java.util.concurrent.SynchronousQueue}
     */
    SYNCHRONOUS_QUEUE() {
        @Override
        public BlockingQueue<Runnable> getWorkQueue(Integer capacity) {
            return Queues.newSynchronousQueue();
        }
    },

    /**
     * {@link java.util.concurrent.ArrayBlockingQueue}
     */
    ARRAY_BLOCKING_QUEUE() {
        @Override
        public BlockingQueue<Runnable> getWorkQueue(Integer capacity) {
            return Queues.newArrayBlockingQueue(capacity);
        }
    },

    /**
     * {@link java.util.concurrent.LinkedBlockingQueue}
     */
    LINKED_BLOCKING_QUEUE() {
        @Override
        public BlockingQueue<Runnable> getWorkQueue(Integer capacity) {
            return (Objects.isNull(capacity) || capacity == 0) ? Queues.newLinkedBlockingQueue() : Queues.newLinkedBlockingQueue(capacity);
        }
    },

    ;

    public abstract BlockingQueue<Runnable> getWorkQueue(Integer capacity);
}
