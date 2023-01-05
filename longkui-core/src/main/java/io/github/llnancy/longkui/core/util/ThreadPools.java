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

package io.github.llnancy.longkui.core.util;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.github.llnancy.longkui.core.config.ThreadPoolConfig;
import io.github.llnancy.longkui.core.exceptions.RpcException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * ThreadPool Utils
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/17
 */
@Slf4j
public final class ThreadPools {

    private ThreadPools() {
    }

    private static final Map<ThreadPoolConfig, ExecutorService> EXECUTOR_SERVICE_MAP = Maps.newConcurrentMap();

    /**
     * create thread pool if absent.
     *
     * @param config thread pool config
     * @return ExecutorService
     */
    public static ExecutorService createThreadPoolIfAbsent(ThreadPoolConfig config) {
        ExecutorService executorService = EXECUTOR_SERVICE_MAP.computeIfAbsent(config, v -> createThreadPool(config));
        if (executorService.isShutdown() || executorService.isTerminated()) {
            EXECUTOR_SERVICE_MAP.remove(config);
            executorService = createThreadPool(config);
            EXECUTOR_SERVICE_MAP.put(config, executorService);
        }
        return executorService;
    }

    /**
     * create thread pool.
     *
     * @param config thread pool config
     * @return ExecutorService
     */
    public static ExecutorService createThreadPool(ThreadPoolConfig config) {
        String threadNameIdentifier = config.getThreadNameIdentifier();
        return new ThreadPoolExecutor(
                config.getCorePoolSize(),
                config.getMaximumPoolSize(),
                config.getKeepAliveTime(),
                config.getUnit(),
                config.getWorkQueue(),
                createThreadFactory(threadNameIdentifier),
                (r, executor) -> {
                    if (!executor.isShutdown()) {
                        try {
                            LOGGER.error("waiting queue is full, putting...");
                            executor.getQueue().put(r);
                        } catch (InterruptedException e) {
                            throw new RpcException("LongKui-" + threadNameIdentifier + " Thread pool is EXHAUSTED!", e);
                        }
                    }
                }
        );
    }

    /**
     * create thread factory
     *
     * @param threadNameIdentifier Identifier thread name
     * @return ThreadFactory
     */
    private static ThreadFactory createThreadFactory(String threadNameIdentifier) {
        if (StringUtils.isNotBlank(threadNameIdentifier)) {
            return new ThreadFactoryBuilder()
                    .setNameFormat("longkui-" + threadNameIdentifier + "-thread-%d")
                    .setDaemon(true)
                    .build();
        }
        return Executors.defaultThreadFactory();
    }

    public static void shutDownAll() {
        for (Map.Entry<ThreadPoolConfig, ExecutorService> entry : EXECUTOR_SERVICE_MAP.entrySet()) {
            ExecutorService executorService = entry.getValue();
            shutdown(executorService);
        }
    }

    /**
     * shutdown ExecutorService
     *
     * @param executorService ExecutorService
     */
    public static void shutdown(ExecutorService executorService) {
        try {
            // 暂停新任务提交
            executorService.shutdown();
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
        } catch (Throwable t) {
            LOGGER.error("The current server thread is interrupted when it is trying to stop the worker threads. This may leave an inconsistent state. Please check the biz logs.", t);
            // 立即关闭
            executorService.shutdownNow();
            // 保留中断状态
            Thread.currentThread().interrupt();
        }
    }
}
