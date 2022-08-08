package com.sunchaser.shushan.rpc.core.balancer.impl;

import com.google.common.collect.Lists;
import com.sunchaser.shushan.rpc.core.balancer.Invoker;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 轮询 & 加权轮询
 * 当每个节点权重都相同时（权重必须大于0），退化成普通轮询。
 * Borrowed from org.apache.dubbo.rpc.cluster.loadbalance.RoundRobinLoadBalance
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/17
 */
public class RoundRobinLoadBalancer extends AbstractLoadBalancer {

    // private final ConcurrentMap<String, WeightedRoundRobin> wrrMap = Maps.newConcurrentMap();

    private final List<WeightedRoundRobin> wrrList = Lists.newArrayList();

    @Override
    protected <T> Invoker<T> doSelect(List<Invoker<T>> invokers, String routeKey) {
        if (CollectionUtils.isEmpty(wrrList)) {
            for (Invoker<T> invoker : invokers) {
                WeightedRoundRobin wrr = new WeightedRoundRobin();
                wrr.setWeight(invoker.getWeight());
                wrrList.add(wrr);
            }
        }
        int totalWeight = 0;
        long maxCurrent = Long.MIN_VALUE;
        Invoker<T> selectedInvoker = null;
        WeightedRoundRobin selectedWrr = null;
        for (int i = 0, size = invokers.size(); i < size; i++) {
            Invoker<T> invoker = invokers.get(i);
            int weight = invoker.getWeight();
            WeightedRoundRobin wrr = wrrList.get(i);
            // WeightedRoundRobin wrr = wrrMap.computeIfAbsent(invoker.toString() -> {
            //     WeightedRoundRobin w = new WeightedRoundRobin();
            //     w.setWeight(weight);
            //     return w;
            // });
            // if (weight != wrr.getWeight()) {
            //     wrr.setWeight(weight);
            // }
            long current = wrr.increaseCurrent();
            if (current > maxCurrent) {
                // 暂存当前权重最大的节点
                maxCurrent = current;
                selectedInvoker = invoker;
                selectedWrr = wrr;
            }
            totalWeight += weight;
        }
        if (Objects.nonNull(selectedInvoker)) {
            // 被选中的节点权重减去总权重
            selectedWrr.sel(totalWeight);
            return selectedInvoker;
        }
        // should not happen here
        return invokers.get(0);
    }

    /**
     * 每个节点对应一个WeightedRoundRobin对象
     */
    static class WeightedRoundRobin {

        /**
         * 配置的权重（不会变化）
         */
        @Setter
        @Getter
        private int weight;

        /**
         * 当前权重，每个节点在每一轮负载均衡中都会进行调整，初始值为0。
         */
        private final AtomicLong current = new AtomicLong(0);

        public long increaseCurrent() {
            return current.addAndGet(weight);
        }

        public void sel(int total) {
            current.addAndGet(-1 * total);
        }
    }
}
