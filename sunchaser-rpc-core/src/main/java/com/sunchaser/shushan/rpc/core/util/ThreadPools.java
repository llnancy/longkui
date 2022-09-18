package com.sunchaser.shushan.rpc.core.util;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.sunchaser.shushan.rpc.core.config.ThreadPoolConfig;
import com.sunchaser.shushan.rpc.core.exceptions.RpcException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.concurrent.*;

/**
 * ThreadPool Utils
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/17
 */
@Slf4j
public class ThreadPools {

    private ThreadPools() {
    }

    private static final Map<String, ExecutorService> EXECUTOR_SERVICE_MAP = Maps.newConcurrentMap();

    public static ExecutorService createThreadPoolIfAbsent(String threadNameIdentifier) {
        return createThreadPoolIfAbsent(threadNameIdentifier, ThreadPoolConfig.createDefaultConfig());
    }

    public static ExecutorService createThreadPoolIfAbsent(String threadNameIdentifier, ThreadPoolConfig threadPoolConfig) {
        ExecutorService executorService = EXECUTOR_SERVICE_MAP.computeIfAbsent(threadNameIdentifier, v -> createThreadPool(threadNameIdentifier, threadPoolConfig));
        if (executorService.isShutdown() || executorService.isTerminated()) {
            EXECUTOR_SERVICE_MAP.remove(threadNameIdentifier);
            executorService = createThreadPool(threadNameIdentifier, threadPoolConfig);
            EXECUTOR_SERVICE_MAP.put(threadNameIdentifier, executorService);
        }
        return executorService;
    }

    public static ExecutorService createThreadPool(String threadNameIdentifier, ThreadPoolConfig threadPoolConfig) {
        return new ThreadPoolExecutor(
                threadPoolConfig.getCorePoolSize(),
                threadPoolConfig.getMaximumPoolSize(),
                threadPoolConfig.getKeepAliveTime(),
                threadPoolConfig.getUnit(),
                threadPoolConfig.getWorkQueue(),
                createThreadFactory(threadNameIdentifier),
                (r, executor) -> {
                    if (!executor.isShutdown()) {
                        try {
                            LOGGER.error("waiting queue is full, putting...");
                            executor.getQueue().put(r);
                        } catch (InterruptedException e) {
                            throw new RpcException("sunchaser-rpc-" + threadNameIdentifier + " Thread pool is EXHAUSTED!", e);
                        }
                    }
                }
        );
    }

    private static ThreadFactory createThreadFactory(String threadNameIdentifier) {
        if (StringUtils.isNotBlank(threadNameIdentifier)) {
            return new ThreadFactoryBuilder()
                    .setNameFormat("sunchaser-rpc-" + threadNameIdentifier + "-thread-%d")
                    .setDaemon(true)
                    .build();
        }
        return Executors.defaultThreadFactory();
    }

    public static void shutDownAll() {
        for (Map.Entry<String, ExecutorService> entry : EXECUTOR_SERVICE_MAP.entrySet()) {
            ExecutorService executorService = entry.getValue();
            // 暂停新任务提交
            executorService.shutdown();
            try {
                // 等待15秒执行未完成的任务
                if (!executorService.awaitTermination(15, TimeUnit.SECONDS)) {
                    LOGGER.error("Interrupt the worker, which may cause some task inconsistent. Please check the biz logs.");
                    // 立即关闭，取消执行未完成的任务
                    executorService.shutdownNow();
                    // 等待任务取消的响应
                    if (!executorService.awaitTermination(15, TimeUnit.SECONDS)) {
                        LOGGER.error("Thread pool can't be shutdown even with interrupting worker threads, which may cause some task inconsistent. Please check the biz logs.");
                    }
                }
            } catch (InterruptedException e) {
                LOGGER.error("The current server thread is interrupted when it is trying to stop the worker threads. This may leave an inconsistent state. Please check the biz logs.");
                // 立即关闭
                executorService.shutdownNow();
                // 保留中断状态
                Thread.currentThread().interrupt();
            }
        }
    }
}
