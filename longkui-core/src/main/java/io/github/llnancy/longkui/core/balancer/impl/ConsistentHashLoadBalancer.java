/*
 * Copyright 2023 LongKui
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

package io.github.llnancy.longkui.core.balancer.impl;

import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import io.github.llnancy.longkui.core.balancer.Node;
import lombok.Getter;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;

/**
 * 一致性哈希
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/15
 */
public class ConsistentHashLoadBalancer extends AbstractLoadBalancer {

    /**
     * ConsistentHash Selector
     *
     * @param <T> Node Type
     */
    static class ConsistentHashSelector<T> {

        /**
         * 默认每个真实节点对应的虚拟节点数
         */
        private static final int DEFAULT_REPLICA_NUMBER = 160;

        /**
         * delta值
         */
        private static final int DELTA = 4;

        /**
         * 哈希环（包含虚拟节点）
         */
        private final NavigableMap<Long, Node<T>> hashRing;

        /**
         * 每个真实节点对应的虚拟节点数量
         */
        @Getter
        private final int replicaNumber;

        /**
         * 用于记录Node集合的唯一hashCode，用该hashCode值来判断Node列表是否发生了变化
         */
        @Getter
        private final int identityHashCode;

        ConsistentHashSelector(List<? extends Node<T>> nodes, int identityHashCode) {
            this(nodes, identityHashCode, DEFAULT_REPLICA_NUMBER);
        }

        ConsistentHashSelector(List<? extends Node<T>> nodes, int identityHashCode, int replicaNumber) {
            this.hashRing = Maps.newTreeMap();
            this.replicaNumber = replicaNumber;
            this.identityHashCode = identityHashCode;
            this.createHashRing(nodes, replicaNumber);
        }

        /**
         * create hash ring with virtual node.
         *
         * @param nodes         sources nodes
         * @param replicaNumber replica number
         */
        private void createHashRing(List<? extends Node<T>> nodes, int replicaNumber) {
            // 构建含虚拟节点的哈希环
            for (Node<T> node : nodes) {
                String address = node.getNode().toString();
                // 默认虚拟节点数replicaNumber=160。
                // 外层循环40次，内层循环4次，共160次。
                for (int i = 0; i < replicaNumber / DELTA; i++) {
                    // 对hashCode+i进行sha256运算，得到一个长度为16的字节数组
                    byte[] digest = Hashing.sha256()
                            .hashString("SHARD" + address + "-NODE-" + i, StandardCharsets.UTF_8)
                            .asBytes();
                    // 对digest部分字节进行4次hash运算，得到4个不同的long型正整数
                    for (int h = 0; h < DELTA; h++) {
                        // h = 0时，取digest的[0, 3]位字节进行位运算
                        // h = 1时，取digest的[4, 7]位字节进行位运算
                        // h = 2时，取digest的[8, 11]位字节进行位运算
                        // h = 3时，取digest的[12, 15]位字节进行位运算
                        long m = hashCode(digest, h);
                        // 放入哈希环
                        hashRing.put(m, node);
                    }
                }
            }
        }

        public Node<T> select(String hashKey) {
            // 计算key的哈希值
            byte[] digest = Hashing.sha256().hashString(hashKey, StandardCharsets.UTF_8).asBytes();
            // 进行匹配
            return selectForKey(hashCode(digest, 0));
        }

        /**
         * select Node by key
         *
         * @param hash key
         * @return Node
         */
        private Node<T> selectForKey(long hash) {
            // 从哈希环中（TreeMap按照key排序）查找第一个hash值大于或等于传入hash值的Node节点对象。
            Map.Entry<Long, Node<T>> entry = hashRing.ceilingEntry(hash);
            if (entry == null) {
                // 传入hash值大于哈希环中所有节点，则取哈希环的第一个节点。
                entry = hashRing.firstEntry();
            }
            return entry.getValue();
        }

        private long hashCode(byte[] digest, int number) {
            return (((long) (digest[3 + number * 4] & 0xFF) << 24)
                    | ((long) (digest[2 + number * 4] & 0xFF) << 16)
                    | ((long) (digest[1 + number * 4] & 0xFF) << 8)
                    | (digest[number * 4] & 0xFF))
                    & 0xFFFFFFFFL;
        }
    }

    private final ConcurrentMap<String, ConsistentHashSelector<?>> selectors = Maps.newConcurrentMap();

    @Override
    @SuppressWarnings("unchecked")
    protected <T> Node<T> doSelect(List<? extends Node<T>> nodes, String routeKey) {
        int nodesHashCode = nodes.hashCode();
        ConsistentHashSelector<T> selector = (ConsistentHashSelector<T>) selectors.get(routeKey);
        if (Objects.isNull(selector) || selector.getIdentityHashCode() != nodesHashCode) {
            selector = new ConsistentHashSelector<>(nodes, nodesHashCode);
            selectors.put(routeKey, selector);
        }
        return selector.select(routeKey);
    }
}
