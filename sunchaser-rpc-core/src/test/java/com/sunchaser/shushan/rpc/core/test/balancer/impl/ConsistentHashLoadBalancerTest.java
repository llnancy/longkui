package com.sunchaser.shushan.rpc.core.test.balancer.impl;

import com.google.common.collect.Lists;
import com.sunchaser.shushan.rpc.core.balancer.Invoker;
import com.sunchaser.shushan.rpc.core.balancer.LoadBalancer;
import com.sunchaser.shushan.rpc.core.balancer.impl.ConsistentHashLoadBalancer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * 一致性哈希测试
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/18
 */
@Slf4j
class ConsistentHashLoadBalancerTest {

    @Test
    public void testSelect() {
        LoadBalancer loadBalancer = new ConsistentHashLoadBalancer();
        for (int i = 0; i < 7; i++) {
            Invoker<String> select = loadBalancer.select(LoadBalancer.wrap(Lists.newArrayList("A", "B", "C")), "Abc" + i);
            LOGGER.info("select: {}", select.getNode());
        }
    }
}