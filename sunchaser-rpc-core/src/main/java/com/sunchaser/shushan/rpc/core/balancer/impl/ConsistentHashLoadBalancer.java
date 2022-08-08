package com.sunchaser.shushan.rpc.core.balancer.impl;

import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import com.sunchaser.shushan.rpc.core.balancer.Invoker;
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

    static class ConsistentHashSelector<T> {

        /**
         * 哈希环（包含虚拟节点）
         */
        private final NavigableMap<Long, Invoker<T>> hashRing;

        /**
         * 每个真实节点包含的虚拟节点个数
         */
        @Getter
        private final int replicaNumber;

        /**
         * 用于记录Invoker集合的hashCode，用该hashCode值来判断Provider列表是否发生了变化
         */
        @Getter
        private final int identityHashCode;

        public ConsistentHashSelector(List<Invoker<T>> invokers, int identityHashCode) {
            this(invokers, identityHashCode, 160);
        }

        public ConsistentHashSelector(List<Invoker<T>> invokers, int identityHashCode, int replicaNumber) {
            this.hashRing = Maps.newTreeMap();
            this.replicaNumber = replicaNumber;
            this.identityHashCode = identityHashCode;
            StringBuilder build = new StringBuilder();
            // 构建含虚拟节点的哈希环
            for (Invoker<T> invoker : invokers) {
                String address = build.append(invoker.getNode().toString())
                        .append(invoker.getWeight())
                        .toString();
                for (int i = 0; i < replicaNumber / 4; i++) {
                    // 对hashCode+i进行sha256运算，得到一个长度为16的字节数组
                    byte[] digest = Hashing.sha256()
                            .hashString("SHARD" + address + "-NODE-" + i, StandardCharsets.UTF_8)
                            .asBytes();
                    // 对digest部分字节进行4次hash运算，得到4个不同的long型正整数
                    for (int h = 0; h < 4; h++) {
                        long m = hash(digest, h);
                        hashRing.put(m, invoker);
                    }
                }
            }
        }

        public Invoker<T> select(String routeKey) {
            // 计算key的哈希值
            byte[] digest = Hashing.sha256().hashString(routeKey, StandardCharsets.UTF_8).asBytes();
            // 进行匹配
            return selectForKey(hash(digest, 0));
        }

        private Invoker<T> selectForKey(long hash) {
            // 从哈希环中（TreeMap按照key排序）查找第一个hash值大于或等于传入hash值的Invoker节点对象。
            Map.Entry<Long, Invoker<T>> entry = hashRing.ceilingEntry(hash);
            if (entry == null) {
                // 传入hash值大于哈希环中所有节点，则取哈希环的第一个节点。
                entry = hashRing.firstEntry();
            }
            return entry.getValue();
        }

        private long hash(byte[] digest, int number) {
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
    protected <T> Invoker<T> doSelect(List<Invoker<T>> invokers, String routeKey) {
        int invokersHashCode = invokers.hashCode();
        ConsistentHashSelector<T> selector = (ConsistentHashSelector<T>) selectors.get(routeKey);
        if (Objects.isNull(selector) || selector.getIdentityHashCode() != invokersHashCode) {
            selector = new ConsistentHashSelector<>(invokers, invokersHashCode);
            selectors.put(routeKey, selector);
        }
        return selector.select(routeKey);
    }
}
