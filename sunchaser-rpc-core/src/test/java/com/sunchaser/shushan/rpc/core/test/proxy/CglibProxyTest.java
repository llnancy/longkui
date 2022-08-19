package com.sunchaser.shushan.rpc.core.test.proxy;

import com.sunchaser.shushan.rpc.core.test.HelloService;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * cglib proxy test
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/8/19
 */
@Slf4j
public class CglibProxyTest {

    public static void main(String[] args) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(HelloService.class);
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                return "cglib Proxy invoke";
            }
        });
        HelloService proxyInstance = (HelloService) enhancer.create();
        String hi = proxyInstance.sayHello("hi");
        LOGGER.info("hi: {}", hi);
    }
}
