package com.sunchaser.shushan.rpc.core.test;

/**
 * hello service impl
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/17
 */
public class HelloServiceImpl implements HelloService {

    @Override
    public String sayHello(String hi) {
        return "Hello:" + hi;
    }

    @Override
    public String sayHi(String hi, Integer time, Long ts) {
        return hi + time + ts;
    }
}
