package com.sunchaser.rpc.core.test;

/**
 * hello service
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/17
 */
public interface HelloService {

    String sayHello(String hi);

    String sayHi(String hi, Integer time, Long ts);
}
