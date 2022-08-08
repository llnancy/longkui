package com.sunchaser.shushan.rpc.core.util;

import com.sunchaser.shushan.rpc.core.exceptions.RpcException;

import javax.annotation.Nonnull;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ThreadPool Utils
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/17
 */
public class ThreadPools {

    private ThreadPools() {
    }

    public static ThreadPoolExecutor createThreadPool(final String source, final int corePoolSize, final int maxPoolSize) {
        return new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                60,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(1000),
                new ThreadFactory() {

                    private final AtomicInteger threadNumber = new AtomicInteger(1);

                    private final ThreadGroup group;

                    private final String namePrefix;

                    {
                        SecurityManager s = System.getSecurityManager();
                        group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
                        namePrefix = "sunchaser-rpc-" + source + "-thread-";
                    }

                    @Override
                    public Thread newThread(@Nonnull Runnable r) {
                        Thread t = new Thread(
                                group,
                                r,
                                namePrefix + threadNumber.getAndIncrement(),
                                0);
                        if (!t.isDaemon())
                            t.setDaemon(true);
                        if (t.getPriority() != Thread.NORM_PRIORITY)
                            t.setPriority(Thread.NORM_PRIORITY);
                        return t;
                    }
                },
                (r, executor) -> {
                    throw new RpcException("sunchaser-rpc-" + source + " Thread pool is EXHAUSTED!");
                }
        );
    }
}
