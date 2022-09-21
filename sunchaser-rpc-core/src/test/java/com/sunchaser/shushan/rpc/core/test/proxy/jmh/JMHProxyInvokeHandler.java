package com.sunchaser.shushan.rpc.core.test.proxy.jmh;

import com.sunchaser.shushan.rpc.core.config.RpcClientConfig;
import javassist.util.proxy.MethodHandler;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * JMH基准测试 method invoke handler
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/8/24
 */
@Getter
@Slf4j
public class JMHProxyInvokeHandler implements InvocationHandler, MethodInterceptor, MethodHandler {

    private final RpcClientConfig rpcClientConfig;

    public JMHProxyInvokeHandler(RpcClientConfig rpcClientConfig) {
        this.rpcClientConfig = rpcClientConfig;
    }

    public Object benchmarkInvoke(Object[] args) {
        return args[0];
    }

    /**
     * JDK动态代理
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // LOGGER.info("jdk benchmarkInvoke, args: {}", Arrays.toString(args));
        return benchmarkInvoke(args);
    }

    /**
     * cglib动态代理
     */
    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        // LOGGER.info("cglib benchmarkInvoke, args: {}", Arrays.toString(args));
        return benchmarkInvoke(args);
    }

    /**
     * javassist动态代理
     */
    @Override
    public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
        // LOGGER.info("javassist benchmarkInvoke, args: {}", Arrays.toString(args));
        return benchmarkInvoke(args);
    }

    /**
     * byte buddy动态代理
     */
    @RuntimeType
    public Object byteBuddyInvoke(@This Object proxy, @Origin Method method, @AllArguments @RuntimeType Object[] args) throws Throwable {
        // LOGGER.info("byte-buddy benchmarkInvoke, args: {}", Arrays.toString(args));
        return benchmarkInvoke(args);
    }
}
