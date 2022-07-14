package com.sunchaser.rpc.core.balancer;

import java.util.List;

/**
 * RPC调用负载均衡抽象接口
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/14
 */
public interface LoadBalancer<T> {

    T select(List<T> servers);
}
