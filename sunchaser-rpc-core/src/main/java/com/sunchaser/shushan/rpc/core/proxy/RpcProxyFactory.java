package com.sunchaser.shushan.rpc.core.proxy;

import com.google.common.collect.Maps;
import net.sf.cglib.proxy.Enhancer;

import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

/**
 * 动态代理实现类工厂
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/8/11
 */
public class RpcProxyFactory {

    public static <T> T getRpcProxyInstance(Class<T> clazz) {
        return RpcProxyEnum.CGLIB.getRpcProxyInstance(clazz);
    }

    public static <T> T getRpcProxyInstance(String rpcProxyImplType, Class<T> clazz) {
        return RpcProxyEnum.match(rpcProxyImplType)
                .getRpcProxyInstance(clazz);
    }

    enum RpcProxyEnum {

        /**
         * JDK
         */
        JDK() {

            private final ConcurrentMap<Class<?>, Object> jdkCache = Maps.newConcurrentMap();

            @SuppressWarnings("unchecked")
            @Override
            <T> T getRpcProxyInstance(Class<T> clazz) {
                if (!clazz.isInterface()) {
                    // throw new IllegalArgumentException(clazz.getName() + " is not an interface");
                    return CGLIB.getRpcProxyInstance(clazz);
                }
                return (T) jdkCache.computeIfAbsent(clazz, proxy -> Proxy.newProxyInstance(
                        Thread.currentThread().getContextClassLoader(),
                        new Class[]{clazz},
                        new JdkProxy(clazz.getName())
                ));
            }
        },

        /**
         * cglib
         */
        CGLIB() {

            private final ConcurrentMap<Class<?>, Object> cglibCache = Maps.newConcurrentMap();

            @SuppressWarnings("unchecked")
            @Override
            <T> T getRpcProxyInstance(Class<T> clazz) {
                return (T) cglibCache.computeIfAbsent(clazz, proxy -> {
                    Enhancer enhancer = new Enhancer();
                    enhancer.setSuperclass(clazz);
                    enhancer.setCallback(new CglibProxy(clazz.getName()));
                    return enhancer.create();
                });
            }
        },

        ;

        private static final Map<String, RpcProxyEnum> ENUM_MAP = Maps.newHashMap();

        static {
            for (RpcProxyEnum value : RpcProxyEnum.values()) {
                ENUM_MAP.put(value.name(), value);
            }
        }

        public static RpcProxyEnum match(String name) {
            return Optional.ofNullable(ENUM_MAP.get(name))
                    .orElse(CGLIB);
        }

        abstract <T> T getRpcProxyInstance(Class<T> clazz);
    }
}