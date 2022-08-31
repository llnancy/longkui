package com.sunchaser.shushan.rpc.core.proxy;

import com.google.common.collect.Maps;
import net.sf.cglib.proxy.Enhancer;

import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

/**
 * 标准策略模式+简单工厂模式实现
 * 策略接口：RpcDynamicProxy
 * 抽象实现类：AbstractRpcDynamicProxy
 * JDK动态代理实现类：JdkRpcDynamicProxy
 * CGLIB动态代理实现类：CglibRpcDynamicProxy
 * <p>
 * rpc dynamic proxy interface
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/8/22
 */
@Deprecated
public interface RpcDynamicProxy {

    /**
     * 创建并获取代理对象
     *
     * @param clazz 被代理对象
     * @param <T>   代理对象的类型
     * @return 代理对象
     */
    <T> T createProxyInstance(Class<T> clazz);

    abstract class AbstractRpcDynamicProxy implements RpcDynamicProxy {

        private final ConcurrentMap<Class<?>, Object> PROXY_CACHE = Maps.newConcurrentMap();

        @Override
        @SuppressWarnings("unchecked")
        public <T> T createProxyInstance(Class<T> clazz) {
            return (T) PROXY_CACHE.computeIfAbsent(clazz, proxy -> doCreateProxyInstance(clazz));
        }

        protected abstract <T> Object doCreateProxyInstance(Class<T> clazz);
    }

    class JdkRpcDynamicProxy extends AbstractRpcDynamicProxy {

        private static final JdkRpcDynamicProxy INSTANCE = new JdkRpcDynamicProxy();

        public static JdkRpcDynamicProxy getInstance() {
            return INSTANCE;
        }

        @Override
        protected <T> Object doCreateProxyInstance(Class<T> clazz) {
            if (!clazz.isInterface()) {
                // throw new IllegalArgumentException(clazz.getName() + " is not an interface");
                return CglibRpcDynamicProxy.getInstance().createProxyInstance(clazz);
            }
            return Proxy.newProxyInstance(
                    Thread.currentThread().getContextClassLoader(),
                    new Class[]{clazz},
                    new ProxyInvokeHandler(clazz)
            );
        }
    }

    class CglibRpcDynamicProxy extends AbstractRpcDynamicProxy {

        private static final CglibRpcDynamicProxy INSTANCE = new CglibRpcDynamicProxy();

        public static CglibRpcDynamicProxy getInstance() {
            return INSTANCE;
        }

        @Override
        protected <T> Object doCreateProxyInstance(Class<T> clazz) {
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(clazz);
            enhancer.setCallback(new ProxyInvokeHandler(clazz));
            return enhancer.create();
        }
    }

    enum RpcDynamicProxyEnum {

        /**
         * JDK
         */
        JDK,

        /**
         * cglib
         */
        CGLIB,
    }

    class RpcDynamicProxyFactory {

        private static final Map<String, RpcDynamicProxy> IMPORT_RDP_MAP = Maps.newHashMap();

        static {
            IMPORT_RDP_MAP.put(RpcDynamicProxyEnum.JDK.name().toLowerCase(), JdkRpcDynamicProxy.getInstance());
            IMPORT_RDP_MAP.put(RpcDynamicProxyEnum.CGLIB.name().toLowerCase(), CglibRpcDynamicProxy.getInstance());
        }

        public static <T> T getRpcProxyInstance(String proxyType, Class<T> clazz) {
            return Optional.ofNullable(IMPORT_RDP_MAP.get(proxyType))
                    .orElse(JdkRpcDynamicProxy.getInstance())
                    .createProxyInstance(clazz);
        }

        public static <T> T getRpcProxyInstance(Class<T> clazz) {
            return JdkRpcDynamicProxy.getInstance()
                    .createProxyInstance(clazz);
        }
    }
}
