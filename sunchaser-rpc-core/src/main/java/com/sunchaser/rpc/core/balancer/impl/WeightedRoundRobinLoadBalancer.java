package com.sunchaser.rpc.core.balancer.impl;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 加权轮询
 * Borrowed from org.apache.dubbo.rpc.cluster.loadbalance.RoundRobinLoadBalance
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/17
 */
public class WeightedRoundRobinLoadBalancer<T> extends AbstractWeightLoadBalancer<T> {

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

    // private final ConcurrentMap<String, WeightedRoundRobin> wrrMap = Maps.newConcurrentMap();

    private final List<WeightedRoundRobin> wrrList = Lists.newArrayList();

    @Override
    protected T doSelect(List<T> servers, List<Integer> weights) {
        if (CollectionUtils.isEmpty(wrrList)) {
            for (int i = 0; i < servers.size(); i++) {
                WeightedRoundRobin wrr = new WeightedRoundRobin();
                wrr.setWeight(weights.get(i));
                wrrList.add(wrr);
            }
        }
        int totalWeight = 0;
        long maxCurrent = Long.MIN_VALUE;
        T selectedServer = null;
        WeightedRoundRobin selectedWrr = null;
        for (int i = 0; i < servers.size(); i++) {
            T server = servers.get(i);
            Integer serverWeight = weights.get(i);
            WeightedRoundRobin wrr = wrrList.get(i);
            // WeightedRoundRobin wrr = wrrMap.computeIfAbsent(server.toString(), k -> {
            //     WeightedRoundRobin w = new WeightedRoundRobin();
            //     w.setWeight(serverWeight);
            //     return w;
            // });
            // if (serverWeight != wrr.getWeight()) {
            //     wrr.setWeight(serverWeight);
            // }
            long current = wrr.increaseCurrent();
            if (current > maxCurrent) {
                // 暂存当前权重最大的节点
                maxCurrent = current;
                selectedServer = server;
                selectedWrr = wrr;
            }
            totalWeight += serverWeight;
        }
        if (Objects.nonNull(selectedServer)) {
            // 被选中的节点权重减去总权重
            selectedWrr.sel(totalWeight);
            return selectedServer;
        }
        // should not happen here
        return servers.get(0);
    }
}
