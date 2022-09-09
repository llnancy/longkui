package com.sunchaser.shushan.rpc.core.test.balancer;

import com.google.common.collect.Lists;
import com.sunchaser.shushan.rpc.core.balancer.LoadBalancer;
import com.sunchaser.shushan.rpc.core.balancer.Node;
import com.sunchaser.shushan.rpc.core.balancer.impl.RoundRobinLoadBalancer;
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
        for (int i = 0; i < 10; i++) {
            Node<String> select = loadBalancer.select(LoadBalancer.wrapWithWeightList(Lists.newArrayList("A", "B", "C"), Lists.newArrayList(5, 2, 3)));
            LOGGER.info("select: {}", select.getNode());
        }
    }

    @Test
    public void testRoundRobin() {
        LoadBalancer loadBalancer = new RoundRobinLoadBalancer();
        for (int i = 0; i < 10; i++) {
            Node<String> select = loadBalancer.select(LoadBalancer.wrapWithDefaultWeight(Lists.newArrayList("A", "B", "C")));
            LOGGER.info("select: {}", select.getNode());
        }
    }
}