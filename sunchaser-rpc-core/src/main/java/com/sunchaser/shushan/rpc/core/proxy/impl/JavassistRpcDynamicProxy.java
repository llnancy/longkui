package com.sunchaser.shushan.rpc.core.proxy.impl;

import com.sunchaser.shushan.rpc.core.config.RpcServiceConfig;
import com.sunchaser.shushan.rpc.core.proxy.ProxyInvokeHandler;
import com.sunchaser.shushan.rpc.core.proxy.RpcDynamicProxy;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;
import lombok.SneakyThrows;

/**
 * a rpc dynamic proxy implementation based on javassist
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/15
 */
public class JavassistRpcDynamicProxy extends AbstractRpcDynamicProxy {

    private static final RpcDynamicProxy INSTANCE = new JavassistRpcDynamicProxy();

    public static RpcDynamicProxy getInstance() {
        return INSTANCE;
    }

    /**
     * doCreateProxyInstance
     *
     * @param rpcServiceConfig rpc service config
     * @return proxy object
     */
    @SneakyThrows
    @Override
    protected Object doCreateProxyInstance(RpcServiceConfig rpcServiceConfig) {
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
}
