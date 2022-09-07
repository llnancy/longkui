package com.sunchaser.shushan.rpc.core.test.balancer;

import com.google.common.collect.Lists;
import com.sunchaser.shushan.rpc.core.balancer.LoadBalancer;
import com.sunchaser.shushan.rpc.core.balancer.Node;
import com.sunchaser.shushan.rpc.core.balancer.impl.RandomLoadBalancer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * RandomLoadBalancer Test
 * 加权随机测试
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/1
 */
@Slf4j
class RandomLoadBalancerTest {

    @Test
    public void testRandom() {
        LoadBalancer loadBalancer = new RandomLoadBalancer();
        for (int i = 0; i < 10; i++) {
            Node<String> select = loadBalancer.select(LoadBalancer.weightWrap(Lists.newArrayList("A", "B", "C")));
            LOGGER.info("select: {}", select.getNode());
        }
    }

    @Test
    public void testWeightedRandom() {
        LoadBalancer loadBalancer = new RandomLoadBalancer();
        for (int i = 0; i < 10; i++) {
            Node<String> select = loadBalancer.select(LoadBalancer.weightWrap(Lists.newArrayList("A", "B", "C"), 5, 2, 3));
            LOGGER.info("select: {}", select.getNode());
        }
    }
}