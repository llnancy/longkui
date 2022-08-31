package com.sunchaser.shushan.rpc.core.proxy;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Maps;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;
import lombok.SneakyThrows;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
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
                        new ProxyInvokeHandler(clazz)
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
                enhancer.setCallback(new ProxyInvokeHandler(clazz));
                return enhancer.create();
            }
        },

        /**
         * javassist
         */
        JAVASSIST() {
            @SneakyThrows({Throwable.class, Exception.class})
            @Override
            protected <T> Object doCreateProxyInstance(Class<T> clazz) {
                ProxyFactory factory = new ProxyFactory();
                // 设置接口
                factory.setInterfaces(new Class[]{clazz});
                // 设置拦截方法过滤器。设置哪些方法调用需要被拦截
                factory.setFilter(m -> true);
                Class<?> proxyClass = factory.createClass();
                ProxyObject proxyObject = (ProxyObject) proxyClass.getDeclaredConstructor()
                        .newInstance();
                proxyObject.setHandler(new ProxyInvokeHandler(clazz));
                return proxyObject;
            }
        },

        /**
         * byteBuddy
         */
        BYTE_BUDDY() {
            @SneakyThrows({Throwable.class, Exception.class})
            @SuppressWarnings("all")
            @Override
            protected <T> Object doCreateProxyInstance(Class<T> clazz) {
                return new ByteBuddy().subclass(clazz)
                        .method(ElementMatchers.isDeclaredBy(clazz))
                        .intercept(MethodDelegation.to(new ProxyInvokeHandler(clazz)))
                        .make()
                        .load(clazz.getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                        .getLoaded()
                        .getDeclaredConstructor()
                        .newInstance();
            }
        }

        ;

        private static final Map<String, RpcDynamicProxyEnum> ENUM_MAP = Maps.newHashMap();

        static {
            for (RpcDynamicProxyEnum value : RpcDynamicProxyEnum.values()) {
                ENUM_MAP.put(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, value.name()), value);
            }
        }

        public static RpcDynamicProxyEnum match(String name) {
            return Optional.ofNullable(ENUM_MAP.get(name))
                    .orElse(JDK);
        }

        /**
         * non-static
         */
        private final ConcurrentMap<Class<?>, Object> PROXY_CACHE = Maps.newConcurrentMap();

        @SuppressWarnings("unchecked")
        public <T> T createProxyInstance(Class<T> clazz) {
            return (T) PROXY_CACHE.computeIfAbsent(clazz, proxy -> doCreateProxyInstance(clazz));
        }

        protected abstract <T> Object doCreateProxyInstance(Class<T> clazz);
    }
}
