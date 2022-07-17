package com.sunchaser.rpc.core.test.balancer.impl;

import com.google.common.collect.Lists;
import com.sunchaser.rpc.core.balancer.LoadBalancer;
import com.sunchaser.rpc.core.balancer.impl.WeightedRoundRobinLoadBalancer;
import org.junit.jupiter.api.Test;

/**
 * WeightedRoundRobinLoadBalancer Test
 * 加权轮询测试
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/18
 */
class WeightedRoundRobinLoadBalancerTest {

    @Test
    public void testSelect() {
        LoadBalancer<String> loadBalancer = new WeightedRoundRobinLoadBalancer<>();
        for (int i = 0; i < 7; i++) {
            System.out.println(loadBalancer.select(Lists.newArrayList("A", "B", "C"), 5, 1, 1));
        }
    }
}