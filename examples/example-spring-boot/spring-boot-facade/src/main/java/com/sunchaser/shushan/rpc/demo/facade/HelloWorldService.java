package com.sunchaser.shushan.rpc.demo.facade;

/**
 * demo
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/21
 */
public interface HelloWorldService {

    String hello(String hello);

    String world(String wor, Integer l, Long d);
}
