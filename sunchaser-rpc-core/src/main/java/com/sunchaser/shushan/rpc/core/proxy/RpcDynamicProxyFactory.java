package com.sunchaser.shushan.rpc.core.proxy;

import com.google.common.collect.Maps;
import net.sf.cglib.proxy.Enhancer;

import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

/**
 * 获取RPC动态代理对象的简单工厂（一种策略模式+简单工厂模式的简洁实现方式）
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/8/11
 */
public class RpcDynamicProxyFactory {

    public static <T> T getRpcProxyInstance(Class<T> clazz) {
        return RpcDynamicProxyEnum.JDK.createProxyInstance(clazz);
    }

    public static <T> T getRpcProxyInstance(String proxyType, Class<T> clazz) {
        return RpcDynamicProxyEnum.match(proxyType)
                .createProxyInstance(clazz);
    }

    public enum RpcDynamicProxyEnum {

        /**
         * JDK
         */
        JDK() {
            @Override
            protected <T> Object doCreateProxyInstance(Class<T> clazz) {
                if (!clazz.isInterface()) {
                    return CGLIB.createProxyInstance(clazz);
                }
                return Proxy.newProxyInstance(
                        Thread.currentThread().getContextClassLoader(),
                        new Class[]{clazz},
                        new JdkMethodInterceptor(clazz)
                );
            }
        },

        /**
         * cglib
         */
        CGLIB() {
            @Override
            protected <T> Object doCreateProxyInstance(Class<T> clazz) {
                Enhancer enhancer = new Enhancer();
                enhancer.setSuperclass(clazz);
                enhancer.setCallback(new CglibMethodInterceptor(clazz));
                return enhancer.create();
            }
        },

        ;

        private static final Map<String, RpcDynamicProxyEnum> ENUM_MAP = Maps.newHashMap();

        static {
            for (RpcDynamicProxyEnum value : RpcDynamicProxyEnum.values()) {
                ENUM_MAP.put(value.name(), value);
            }
        }

        public static RpcDynamicProxyEnum match(String name) {
            return Optional.ofNullable(ENUM_MAP.get(name))
                    .orElse(JDK);
        }

        private static final ConcurrentMap<Class<?>, Object> PROXY_CACHE = Maps.newConcurrentMap();

        @SuppressWarnings("unchecked")
        <T> T createProxyInstance(Class<T> clazz) {
            return (T) PROXY_CACHE.computeIfAbsent(clazz, proxy -> doCreateProxyInstance(clazz));
        }

        protected abstract <T> Object doCreateProxyInstance(Class<T> clazz);
    }
}
