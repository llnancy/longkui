package com.sunchaser.shushan.rpc.core.util;

import com.google.common.collect.Maps;

import java.lang.reflect.Modifier;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;

/**
 * 单例对象工厂
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/8/15
 */
public class SingletonFactory {

    private static final ConcurrentMap<String, Object> SINGLETON_OBJECT_MAP = Maps.newConcurrentMap();

    private SingletonFactory() {
    }

    public static <T> T getSingletonObject(Class<T> clazz) {
        if (Objects.isNull(clazz)) {
            throw new IllegalArgumentException("clazz must not be null.");
        }
        if (clazz.isInterface()) {
            throw new IllegalArgumentException(clazz + " must not be an interface.");
        }
        if (Modifier.isAbstract(clazz.getModifiers())) {
            throw new IllegalArgumentException(clazz + " must not be an abstract class.");
        }
        String key = clazz.toString();
        if (SINGLETON_OBJECT_MAP.containsKey(key)) {
            return clazz.cast(SINGLETON_OBJECT_MAP.get(key));
        }
        return clazz.cast(SINGLETON_OBJECT_MAP.computeIfAbsent(key, obj -> {
            try {
                return clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }));
    }
}
