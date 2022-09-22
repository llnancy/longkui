/*
 * Copyright 2022 SunChaser
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

package com.sunchaser.shushan.rpc.core.balancer;

import com.google.common.collect.Lists;
import com.sunchaser.shushan.rpc.core.extension.SPI;
import com.sunchaser.shushan.rpc.core.util.ListUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

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
@SPI
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
     * @param hashKey 哈希key（一致性哈希算法中用于对请求进行哈希的key）
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
     * 将资源列表sources、权重&预热时间weightPairList包装为WeightNode列表，默认权重为1，默认预热时间为10分钟
     *
     * @param sources        资源列表
     * @param weightPairList 权重&预热时间Pair列表
     * @param <T>            资源类型
     * @return WeightNode列表
     */
    static <T> List<? extends Node<T>> wrap(List<T> sources, List<ImmutablePair<Integer, Integer>> weightPairList) {
        List<Integer> weightList = Lists.newArrayList(weightPairList.size());
        List<Integer> warmupList = Lists.newArrayList(weightPairList.size());
        for (ImmutablePair<Integer, Integer> pair : weightPairList) {
            weightList.add(pair.getLeft());
            warmupList.add(pair.getRight());
        }
        return wrap(sources, weightList, warmupList);
    }

    /**
     * 将资源列表sources、权重列表weightList和预热时间列表warmupList包装为WeightNode列表，默认权重为1，默认预热时间为10分钟
     *
     * @param sources    资源列表
     * @param weightList 权重列表
     * @param warmupList 预热时间列表
     * @param <T>        资源类型
     * @return WeightNode列表
     */
    static <T> List<? extends Node<T>> wrap(List<T> sources, List<Integer> weightList, List<Integer> warmupList) {
        if (CollectionUtils.isEmpty(sources)) {
            return Collections.emptyList();
        }
        return IntStream.range(0, sources.size())
                .mapToObj(i -> WeightNode.<T>builder()
                        .node(sources.get(i))
                        .weight(Math.max(ListUtils.get(weightList, i, Weightable.DEFAULT_WEIGHT), Weightable.DEFAULT_WEIGHT))
                        .warmup(Math.max(ListUtils.get(warmupList, i, Weightable.DEFAULT_WARMUP), Weightable.DEFAULT_WARMUP))
                        .build()
                )
                .collect(Collectors.toList());
    }

    /**
     * 将资源列表sources和权重列表weightList包装为WeightNode列表，默认预热时间为10分钟
     *
     * @param sources    资源列表
     * @param weightList 权重列表
     * @param <T>        资源类型
     * @return WeightNode列表
     */
    static <T> List<? extends Node<T>> wrapWithWeightList(List<T> sources, List<Integer> weightList) {
        return wrap(sources, weightList, Collections.emptyList());
    }

    /**
     * 将资源列表sources和预热时间列表warmupList包装为WeightNode列表，默认预热时间为10分钟
     *
     * @param sources    资源列表
     * @param warmupList 预热时间列表
     * @param <T>        资源类型
     * @return WeightNode列表
     */
    static <T> List<? extends Node<T>> wrapWithWarmupList(List<T> sources, List<Integer> warmupList) {
        return wrap(sources, Collections.emptyList(), warmupList);
    }

    /**
     * 将资源列表sources包装为WeightNode列表，默认权重为1，默认预热时间为10分钟
     *
     * @param sources 资源列表
     * @param <T>     资源类型
     * @return WeightNode列表
     */
    static <T> List<? extends Node<T>> wrapWithDefaultWeight(List<T> sources) {
        return wrap(sources, Collections.emptyList(), Collections.emptyList());
    }
}
