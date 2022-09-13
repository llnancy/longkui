package com.sunchaser.shushan.rpc.core.proxy;

import com.google.common.collect.Maps;
import com.sunchaser.shushan.rpc.core.config.RpcServiceConfig;
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
     * 根据RpcServiceConfig创建并获取代理对象
     *
     * @param rpcServiceConfig rpc service config
     * @param <T>              代理对象的类型
     * @return 代理对象
     */
    <T> T createProxyInstance(RpcServiceConfig rpcServiceConfig);

    abstract class AbstractRpcDynamicProxy implements RpcDynamicProxy {

        private final ConcurrentMap<RpcServiceConfig, Object> PROXY_CACHE = Maps.newConcurrentMap();

        @Override
        @SuppressWarnings("unchecked")
        public <T> T createProxyInstance(RpcServiceConfig rpcServiceConfig) {
            return (T) PROXY_CACHE.computeIfAbsent(rpcServiceConfig, proxy -> doCreateProxyInstance(rpcServiceConfig));
        }

        protected abstract Object doCreateProxyInstance(RpcServiceConfig rpcServiceConfig);
    }

    class JdkRpcDynamicProxy extends AbstractRpcDynamicProxy {

        private static final JdkRpcDynamicProxy INSTANCE = new JdkRpcDynamicProxy();

        public static JdkRpcDynamicProxy getInstance() {
            return INSTANCE;
        }

        @Override
        protected Object doCreateProxyInstance(RpcServiceConfig rpcServiceConfig) {
            Class<?> clazz = rpcServiceConfig.getTargetClass();
            if (!clazz.isInterface()) {
                // throw new IllegalArgumentException(clazz.getName() + " is not an interface");
                return CglibRpcDynamicProxy.getInstance().createProxyInstance(rpcServiceConfig);
            }
            return Proxy.newProxyInstance(
                    Thread.currentThread().getContextClassLoader(),
                    new Class[]{clazz},
                    new ProxyInvokeHandler(rpcServiceConfig)
            );
        }
    }

    class CglibRpcDynamicProxy extends AbstractRpcDynamicProxy {

        private static final CglibRpcDynamicProxy INSTANCE = new CglibRpcDynamicProxy();

        public static CglibRpcDynamicProxy getInstance() {
            return INSTANCE;
        }

        @Override
        protected Object doCreateProxyInstance(RpcServiceConfig rpcServiceConfig) {
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(rpcServiceConfig.getTargetClass());
            enhancer.setCallback(new ProxyInvokeHandler(rpcServiceConfig));
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

        public static <T> T getRpcProxyInstance(String proxyType, RpcServiceConfig rpcServiceConfig) {
            return Optional.ofNullable(IMPORT_RDP_MAP.get(proxyType))
                    .orElse(JdkRpcDynamicProxy.getInstance())
                    .createProxyInstance(rpcServiceConfig);
        }

        public static <T> T getRpcProxyInstance(RpcServiceConfig rpcServiceConfig) {
            return JdkRpcDynamicProxy.getInstance()
                    .createProxyInstance(rpcServiceConfig);
        }
    }
}
