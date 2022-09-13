package com.sunchaser.shushan.rpc.core.proxy;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Maps;
import com.sunchaser.shushan.rpc.core.config.RpcServiceConfig;
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

    public static <T> T getRpcProxyInstance(RpcServiceConfig rpcServiceConfig) {
        return RpcDynamicProxyEnum.JDK.createProxyInstance(rpcServiceConfig);
    }

    public static <T> T getRpcProxyInstance(String proxyType, RpcServiceConfig rpcServiceConfig) {
        return RpcDynamicProxyEnum.match(proxyType)
                .createProxyInstance(rpcServiceConfig);
    }

    public enum RpcDynamicProxyEnum {

        /**
         * JDK
         */
        JDK() {
            @Override
            Object doCreateProxyInstance(RpcServiceConfig rpcServiceConfig) {
                Class<?> clazz = rpcServiceConfig.getTargetClass();
                if (!clazz.isInterface()) {
                    return CGLIB.createProxyInstance(rpcServiceConfig);
                }
                return Proxy.newProxyInstance(
                        Thread.currentThread().getContextClassLoader(),
                        new Class[]{clazz},
                        new ProxyInvokeHandler(rpcServiceConfig)
                );
            }
        },

        /**
         * cglib
         */
        CGLIB() {
            @Override
            Object doCreateProxyInstance(RpcServiceConfig rpcServiceConfig) {
                Enhancer enhancer = new Enhancer();
                enhancer.setSuperclass(rpcServiceConfig.getTargetClass());
                enhancer.setCallback(new ProxyInvokeHandler(rpcServiceConfig));
                return enhancer.create();
            }
        },

        /**
         * javassist
         */
        JAVASSIST() {
            @SneakyThrows({Throwable.class, Exception.class})
            @Override
            Object doCreateProxyInstance(RpcServiceConfig rpcServiceConfig) {
                ProxyFactory factory = new ProxyFactory();
                // 设置接口
                factory.setInterfaces(new Class[]{rpcServiceConfig.getTargetClass()});
                // 设置拦截方法过滤器。设置哪些方法调用需要被拦截
                factory.setFilter(m -> true);
                Class<?> proxyClass = factory.createClass();
                ProxyObject proxyObject = (ProxyObject) proxyClass.getDeclaredConstructor()
                        .newInstance();
                proxyObject.setHandler(new ProxyInvokeHandler(rpcServiceConfig));
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
            Object doCreateProxyInstance(RpcServiceConfig rpcServiceConfig) {
                Class<?> clazz = rpcServiceConfig.getTargetClass();
                return new ByteBuddy().subclass(clazz)
                        .method(ElementMatchers.isDeclaredBy(clazz))
                        .intercept(MethodDelegation.to(new ProxyInvokeHandler(rpcServiceConfig)))
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
        private final ConcurrentMap<RpcServiceConfig, Object> PROXY_CACHE = Maps.newConcurrentMap();

        @SuppressWarnings("unchecked")
        <T> T createProxyInstance(RpcServiceConfig rpcServiceConfig) {
            return (T) PROXY_CACHE.computeIfAbsent(rpcServiceConfig, proxy -> doCreateProxyInstance(rpcServiceConfig));
        }

        abstract Object doCreateProxyInstance(RpcServiceConfig rpcServiceConfig);
    }
}
