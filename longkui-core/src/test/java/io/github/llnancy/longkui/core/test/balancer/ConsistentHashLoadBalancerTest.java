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
import io.github.llnancy.longkui.core.balancer.Node;
import io.github.llnancy.longkui.core.balancer.LoadBalancer;
import io.github.llnancy.longkui.core.balancer.impl.ConsistentHashLoadBalancer;
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
            Node<String> select = loadBalancer.select(LoadBalancer.wrap(Lists.newArrayList("A", "B", "C")), "Abc" + i);
            LOGGER.info("select: {}", select.getNode());
        }
    }
}