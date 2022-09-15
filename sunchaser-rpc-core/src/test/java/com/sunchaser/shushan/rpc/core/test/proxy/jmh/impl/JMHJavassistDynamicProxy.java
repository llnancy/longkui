package com.sunchaser.shushan.rpc.core.test.proxy.jmh.impl;

import com.sunchaser.shushan.rpc.core.config.RpcFrameworkConfig;
import com.sunchaser.shushan.rpc.core.config.RpcServiceConfig;
import com.sunchaser.shushan.rpc.core.proxy.DynamicProxy;
import com.sunchaser.shushan.rpc.core.test.proxy.jmh.JMHProxyInvokeHandler;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;
import lombok.SneakyThrows;

/**
 * a rpc dynamic proxy implementation based on javassist
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/15
 */
public class JMHJavassistDynamicProxy extends JMHAbstractDynamicProxy {

    private static final DynamicProxy INSTANCE = new JMHJavassistDynamicProxy();

    public static DynamicProxy getInstance() {
        return INSTANCE;
    }

    /**
     * doCreateProxyInstance
     *
     * @param rpcFrameworkConfig rpc framework config
     * @return proxy object
     */
    @SneakyThrows
    @Override
    protected Object doCreateProxyInstance(RpcFrameworkConfig rpcFrameworkConfig) {
        RpcServiceConfig rpcServiceConfig = rpcFrameworkConfig.getRpcServiceConfig();
        ProxyFactory factory = new ProxyFactory();
        // 设置接口
        factory.setInterfaces(new Class[]{rpcServiceConfig.getTargetClass()});
        // 设置拦截方法过滤器。设置哪些方法调用需要被拦截
        factory.setFilter(m -> true);
        Class<?> proxyClass = factory.createClass();
        ProxyObject proxyObject = (ProxyObject) proxyClass.getDeclaredConstructor()
                .newInstance();
        proxyObject.setHandler(new JMHProxyInvokeHandler(rpcFrameworkConfig));
        return proxyObject;
    }
}
