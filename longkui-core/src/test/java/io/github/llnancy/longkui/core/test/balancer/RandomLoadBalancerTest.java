/*
 * Copyright 2022 LongKui
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.llnancy.longkui.core.test.balancer;

import com.google.common.collect.Lists;
import io.github.llnancy.longkui.core.balancer.LoadBalancer;
import io.github.llnancy.longkui.core.balancer.Node;
import io.github.llnancy.longkui.core.balancer.impl.RandomLoadBalancer;
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
            Node<String> select = loadBalancer.select(LoadBalancer.wrapWithDefaultWeight(Lists.newArrayList("A", "B", "C")));
            LOGGER.info("select: {}", select.getNode());
        }
    }

    @Test
    public void testWeightedRandom() {
        LoadBalancer loadBalancer = new RandomLoadBalancer();
        for (int i = 0; i < 10; i++) {
            Node<String> select = loadBalancer.select(LoadBalancer.wrapWithWeightList(Lists.newArrayList("A", "B", "C"), Lists.newArrayList(5, 2, 3)));
            LOGGER.info("select: {}", select.getNode());
        }
    }
}