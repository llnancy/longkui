package com.sunchaser.rpc.core.balancer;

import java.util.List;

/**
 * RPC调用 负载均衡策略接口
 * 负载均衡策略：随机、轮询、一致性哈希、加权随机、最少活跃调用数、加权轮询、最短响应时间、LRU、LFU
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/14
 */
public interface LoadBalancer<T> {

    T select(List<T> servers);
}
