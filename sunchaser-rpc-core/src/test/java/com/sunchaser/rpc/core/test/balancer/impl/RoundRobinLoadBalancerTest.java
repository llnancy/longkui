package com.sunchaser.rpc.core.test.balancer.impl;

import com.google.common.collect.Lists;
import com.sunchaser.rpc.core.balancer.Invoker;
import com.sunchaser.rpc.core.balancer.LoadBalancer;
import com.sunchaser.rpc.core.balancer.impl.RoundRobinLoadBalancer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * WeightedRoundRobinLoadBalancer Test
 * 加权轮询测试
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/18
 */
@Slf4j
class RoundRobinLoadBalancerTest {

    @Test
    public void testWeightedRoundRobin() {
        LoadBalancer loadBalancer = new RoundRobinLoadBalancer();
        for (int i = 0; i < 7; i++) {
            Invoker<String> select = loadBalancer.select(LoadBalancer.wrap(Lists.newArrayList("A", "B", "C"), 5, 1, 1));
            log.info("select: {}", select.getNode());
        }
    }

    @Test
    public void testRoundRobin() {
        LoadBalancer loadBalancer = new RoundRobinLoadBalancer();
        for (int i = 0; i < 7; i++) {
            Invoker<String> select = loadBalancer.select(LoadBalancer.wrap(Lists.newArrayList("A", "B", "C"), 1, 1, 1));
            log.info("select: {}", select.getNode());
        }
    }
}