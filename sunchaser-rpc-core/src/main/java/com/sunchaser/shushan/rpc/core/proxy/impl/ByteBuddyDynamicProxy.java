package com.sunchaser.shushan.rpc.core.proxy.impl;

import com.sunchaser.shushan.rpc.core.config.RpcFrameworkConfig;
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
     * @param rpcFrameworkConfig rpc framework config
     * @return proxy object
     */
    @SuppressWarnings("all")
    @SneakyThrows
    @Override
    protected Object doCreateProxyInstance(RpcFrameworkConfig rpcFrameworkConfig) {
        RpcServiceConfig rpcServiceConfig = rpcFrameworkConfig.getRpcServiceConfig();
        Class<?> clazz = rpcServiceConfig.getTargetClass();
        return new ByteBuddy().subclass(clazz)
                .method(ElementMatchers.isDeclaredBy(clazz))
                .intercept(MethodDelegation.to(new ProxyInvokeHandler(rpcFrameworkConfig)))
                .make()
                .load(clazz.getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                .getLoaded()
                .getDeclaredConstructor()
                .newInstance();
    }
}
