package com.sunchaser.shushan.rpc.core.util;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * rpc 服务提供者实例对象缓存
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/13
 */
public final class BeanFactory {

    private BeanFactory() {
    }

    private static final Map<String, Object> BEANS_MAP = Maps.newHashMap();

    public static void register(String serviceName, Object service) {
        BEANS_MAP.put(serviceName, service);
    }

    public static Object getBean(String serviceName) {
        return BEANS_MAP.get(serviceName);
    }
}
