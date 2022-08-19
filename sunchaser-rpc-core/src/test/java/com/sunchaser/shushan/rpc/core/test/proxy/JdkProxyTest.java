package com.sunchaser.shushan.rpc.core.test.proxy;

import com.sunchaser.shushan.rpc.core.test.HelloService;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * jdk proxy test
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/8/19
 */
@Slf4j
public class JdkProxyTest {

    public static void main(String[] args) {
        HelloService proxyInstance = (HelloService) Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class[]{HelloService.class},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        return "JDK Proxy invoke";
                    }
                }
        );
        String hi = proxyInstance.sayHello("hi");
        LOGGER.info("hi: {}", hi);
    }
}
