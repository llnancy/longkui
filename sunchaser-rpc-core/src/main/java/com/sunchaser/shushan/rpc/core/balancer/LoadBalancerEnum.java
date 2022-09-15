package com.sunchaser.shushan.rpc.core.balancer;

/**
 * LoadBalancer Enum
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/15
 */
public enum LoadBalancerEnum {

    /**
     * 负载均衡算法枚举
     */
    RANDOM, ROUND_ROBIN, CONSISTENT_HASH
}
