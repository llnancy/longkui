package com.sunchaser.shushan.rpc.core.proxy.impl;

import com.sunchaser.shushan.rpc.core.config.RpcClientConfig;
import com.sunchaser.shushan.rpc.core.config.RpcServiceConfig;
import com.sunchaser.shushan.rpc.core.proxy.DynamicProxy;
import com.sunchaser.shushan.rpc.core.proxy.ProxyInvokeHandler;
import lombok.SneakyThrows;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

/**
 * a dynamic proxy implementation based on Byte Buddy
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/15
 */
public class ByteBuddyDynamicProxy extends AbstractDynamicProxy {

    private static final DynamicProxy INSTANCE = new ByteBuddyDynamicProxy();

    public static DynamicProxy getInstance() {
        return INSTANCE;
    }

    /**
     * doCreateProxyInstance
     *
     * @param rpcClientConfig rpc client config
     * @return proxy object
     */
    @SuppressWarnings("all")
    @SneakyThrows
    @Override
    protected Object doCreateProxyInstance(RpcClientConfig rpcClientConfig) {
        RpcServiceConfig rpcServiceConfig = rpcClientConfig.getRpcServiceConfig();
        Class<?> clazz = rpcServiceConfig.getTargetClass();
        return new ByteBuddy().subclass(clazz)
                .method(ElementMatchers.isDeclaredBy(clazz))
                .intercept(MethodDelegation.to(new ProxyInvokeHandler(rpcClientConfig)))
                .make()
                .load(clazz.getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                .getLoaded()
                .getDeclaredConstructor()
                .newInstance();
    }
}
