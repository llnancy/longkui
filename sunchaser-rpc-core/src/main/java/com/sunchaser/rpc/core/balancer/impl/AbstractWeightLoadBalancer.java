package com.sunchaser.rpc.core.balancer.impl;

import com.sunchaser.rpc.core.balancer.LoadBalancer;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Arrays;
import java.util.List;

/**
 * 抽象加权负载均衡
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/17
 */
public abstract class AbstractWeightLoadBalancer<T> implements LoadBalancer<T> {

    @Override
    public T select(List<T> servers, Integer... weights) {
        if (CollectionUtils.isEmpty(servers)) {
            return null;
        }
        if (servers.size() == 1) {
            return servers.get(0);
        }
        if (weights.length == 0) {
            // init default weight
            weights = new Integer[servers.size()];
            Arrays.fill(weights, 0);
        }
        if (weights.length < servers.size()) {
            // append zero
            Integer[] original = weights;
            weights = Arrays.copyOf(original, servers.size());
            Arrays.fill(weights, original.length, weights.length, 0);
        }
        if (weights.length > servers.size()) {
            // skip
            weights = Arrays.copyOf(weights, servers.size());
        }
        return doSelect(servers, Arrays.asList(weights));
    }

    protected abstract T doSelect(List<T> servers, List<Integer> weights);
}
