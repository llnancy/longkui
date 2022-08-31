package com.sunchaser.shushan.rpc.core.balancer;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
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

    default <T> Node<T> select(List<? extends Node<T>> nodes) {
        return select(nodes, StringUtils.EMPTY);
    }

    <T> Node<T> select(List<? extends Node<T>> nodes, String routeKey);

    static <T> List<Node<T>> wrap(List<T> sources) {
        return Optional.ofNullable(sources)
                .map(col -> col.stream()
                        .map(el -> (Node<T>) Node.<T>builder()
                                .node(el)
                                .build()
                        )
                        .collect(Collectors.toList())
                ).orElse(Collections.emptyList());
    }

    static <T> List<? extends Node<T>> weightWrap(List<T> sources, Integer... weights) {
        if (CollectionUtils.isEmpty(sources)) {
            return Collections.emptyList();
        }
        return IntStream.range(0, sources.size())
                .mapToObj(i -> WeightNode.<T>builder()
                        .node(sources.get(i))
                        .weight(ArrayUtils.get(weights, i, WeightNode.DEFAULT_WEIGHT))
                        .build()
                )
                .collect(Collectors.toList());
    }
}
