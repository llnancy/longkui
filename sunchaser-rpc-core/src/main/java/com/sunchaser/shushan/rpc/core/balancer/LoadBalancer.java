package com.sunchaser.shushan.rpc.core.balancer;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;

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

    /**
     * 负载均衡
     *
     * @param nodes Node列表
     * @param <T>   负载均衡的对象类型
     * @return 负载均衡算法选出的Node
     */
    <T> Node<T> select(List<? extends Node<T>> nodes);

    /**
     * 负载均衡：一致性哈希算法
     *
     * @param nodes   Node列表
     * @param hashKey 哈希key（用于一致性哈希算法的key）
     * @param <T>     负载均衡的对象类型
     * @return 负载均衡算法选出的Node
     */
    <T> Node<T> select(List<? extends Node<T>> nodes, String hashKey);

    /**
     * 将资源列表包装为Node列表
     *
     * @param sources 资源列表
     * @param <T>     资源类型
     * @return Node列表
     */
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

    /**
     * 将资源列表sources和权重列表weights包装为WeightNode列表，默认权重为1
     *
     * @param sources 资源列表
     * @param weights 权重列表 可变参数
     * @param <T>     资源类型
     * @return WeightNode列表
     */
    static <T> List<? extends Node<T>> weightWrap(List<T> sources, Integer... weights) {
        if (CollectionUtils.isEmpty(sources)) {
            return Collections.emptyList();
        }
        return IntStream.range(0, sources.size())
                .mapToObj(i -> WeightNode.<T>builder()
                        .node(sources.get(i))
                        .weight(Math.max(ArrayUtils.get(weights, i, WeightNode.DEFAULT_WEIGHT), WeightNode.DEFAULT_WEIGHT))
                        .build()
                )
                .collect(Collectors.toList());
    }

    /**
     * 将资源列表sources和权重列表weights包装为WeightNode列表，默认权重为1
     *
     * @param sources 资源列表
     * @param weights 权重列表 java.util.List
     * @param <T>     资源类型
     * @return WeightNode列表
     */
    static <T> List<? extends Node<T>> weightWrap(List<T> sources, List<Integer> weights) {
        return weightWrap(sources, weights.toArray(new Integer[0]));
    }
}
