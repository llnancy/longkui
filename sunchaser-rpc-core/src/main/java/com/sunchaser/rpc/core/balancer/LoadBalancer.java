package com.sunchaser.rpc.core.balancer;

import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * RPC调用 负载均衡策略接口
 * 负载均衡策略：随机、轮询、一致性哈希、加权随机、最少活跃调用数、加权轮询、最短响应时间、LRU、LFU
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/14
 */
public interface LoadBalancer {

    default <T> Invoker<T> select(List<Invoker<T>> invokers) {
        return select(invokers, StringUtils.EMPTY);
    }

    <T> Invoker<T> select(List<Invoker<T>> invokers, String routeKey);

    static <T> List<Invoker<T>> wrap(Collection<T> sources) {
        return Optional.ofNullable(sources)
                .map(col -> col.stream()
                        .map(el -> Invoker.<T>builder()
                                .node(el)
                                .build()
                        )
                        .collect(Collectors.toList())
                ).orElse(Collections.emptyList());
    }

    static <T> List<Invoker<T>> wrap(List<T> sources, int... weights) {
        sources = Optional.ofNullable(sources)
                .orElse(Collections.emptyList());
        if (weights.length == 0) {
            return wrap(sources);
        }
        int size = sources.size();
        if (weights.length < size) {
            // append
            int[] original = weights;
            weights = Arrays.copyOf(original, size);
            Arrays.fill(weights, original.length, weights.length, 1);
        }
        if (weights.length > size && size > 0) {
            // skip
            weights = Arrays.copyOf(weights, size);
        }
        final List<T> finalSources = sources;
        final int[] finalWeights = weights;
        return IntStream.range(0, size)
                .mapToObj(i -> Invoker.<T>builder()
                        .node(finalSources.get(i))
                        .weight(finalWeights[i])
                        .build()
                )
                .collect(Collectors.toList());
    }
}
