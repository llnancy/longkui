package com.sunchaser.shushan.rpc.core.test.proxy;

import com.sunchaser.shushan.rpc.core.test.HelloService;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.Method;

/**
 * byte buddy proxy test
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/8/25
 */
@Slf4j
public class ByteBuddyProxyTest {

    public static class ByteBuddyMethodHandler {

        @RuntimeType
        public Object byteBuddyInvoke(@This Object proxy,@Origin Method method, @AllArguments @RuntimeType Object[] args) throws Throwable {
            return "byte buddy Proxy invoke";
        }
    }

    public static void main(String[] args) throws Exception {
        HelloService helloService = new ByteBuddy().subclass(HelloService.class)
                .method(ElementMatchers.isDeclaredBy(HelloService.class))// 拦截哪些方法
                .intercept(MethodDelegation.to(new ByteBuddyMethodHandler()))// 拦截方法处理器
                .make()
                .load(HelloService.class.getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                .getLoaded()
                .getDeclaredConstructor()
                .newInstance();
        String hi = helloService.sayHello("hi");
        LOGGER.info("hi: {}", hi);
    }
}
