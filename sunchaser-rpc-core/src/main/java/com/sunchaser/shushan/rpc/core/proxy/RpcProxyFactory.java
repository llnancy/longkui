package com.sunchaser.shushan.rpc.core.proxy;

import com.google.common.collect.Maps;
import com.sunchaser.shushan.rpc.core.registry.Registry;
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

    public static <T> T getRpcProxyInstance(String rpcProxyImplType, Class<T> clazz, Registry registry) {
        return RpcProxyEnum.match(rpcProxyImplType)
                .getRpcProxyInstance(clazz, registry);
    }

    enum RpcProxyEnum {

        /**
         * JDK
         */
        JDK() {

            private final ConcurrentMap<Class<?>, Object> jdkCache = Maps.newConcurrentMap();

            @SuppressWarnings("unchecked")
            @Override
            <T> T getRpcProxyInstance(Class<T> clazz, Registry registry) {
                return (T) jdkCache.computeIfAbsent(clazz, proxy -> Proxy.newProxyInstance(
                        Thread.currentThread().getContextClassLoader(),
                        new Class[]{clazz},
                        new JdkProxy(clazz.getName(), registry)
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
            <T> T getRpcProxyInstance(Class<T> clazz, Registry registry) {
                return (T) cglibCache.computeIfAbsent(clazz, proxy -> {
                    Enhancer enhancer = new Enhancer();
                    enhancer.setSuperclass(clazz);
                    enhancer.setCallback(new CglibProxy(clazz.getName(), registry));
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

        abstract <T> T getRpcProxyInstance(Class<T> clazz, Registry registry);
    }
}
