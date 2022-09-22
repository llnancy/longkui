package com.sunchaser.shushan.rpc.boot.common;

import com.sunchaser.shushan.rpc.core.config.ThreadPoolConfig;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.util.Assert;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Configurer Support
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/22
 */
public class Configurers {

    public static void configureThreadPool(PropertyMapper map,
                                           Supplier<ThreadPoolProperties> source,
                                           Supplier<ThreadPoolConfig> target) {
        Assert.notNull(source, "Supplier source must not be null");
        Assert.notNull(target, "Supplier target must not be null");
        configureThreadPool(map, source.get(), target.get());
    }

    /**
     * Configure the specified {@link ThreadPoolConfig}. The config can be further tuned
     * and default configs can be overridden.
     *
     * @param map        PropertyMapper
     * @param properties source properties
     * @param config     the {@link ThreadPoolConfig} instance to configure
     */
    public static void configureThreadPool(PropertyMapper map,
                                           ThreadPoolProperties properties,
                                           ThreadPoolConfig config) {
        if (Objects.isNull(properties)) {
            return;
        }
        map.from(properties::getThreadNameIdentifier)
                .whenHasText()
                .to(config::setThreadNameIdentifier);
        map.from(properties::getCorePoolSize)
                .whenNonNull()
                .to(config::setCorePoolSize);
        map.from(properties::getMaximumPoolSize)
                .whenNonNull()
                .to(config::setMaximumPoolSize);
        map.from(properties::getKeepAliveTime)
                .whenNonNull()
                .to(config::setKeepAliveTime);
        map.from(properties.getUnit())
                .whenNonNull()
                .to(config::setUnit);
        map.from(properties.getWorkQueueCapacity())
                .whenNonNull()
                .to(config::setWorkQueueCapacity);
        map.from(properties.getWorkQueueType())
                .whenInstanceOf(WorkQueueType.class)
                .as(el -> el.getWorkQueue(properties.getWorkQueueCapacity()))
                .to(config::setWorkQueue);
    }
}
