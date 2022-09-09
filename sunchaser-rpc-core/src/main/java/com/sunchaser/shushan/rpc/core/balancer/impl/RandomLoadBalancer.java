package com.sunchaser.shushan.rpc.core.balancer.impl;

import com.sunchaser.shushan.rpc.core.balancer.Node;
import com.sunchaser.shushan.rpc.core.balancer.WeightNode;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 随机 & 加权随机
 * 当所有权重都相同时，退化为普通随机。
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/17
 */
public class RandomLoadBalancer extends AbstractLoadBalancer {

    @Override
    protected <T> Node<T> doSelect(List<? extends Node<T>> nodes) {
        int length = nodes.size();
        // 每个Node的权重是否全部相同的标识（如果一样则退化成简单随机）
        boolean sameWeight = true;
        // 权重区间（存放每次权重累加后的值）
        int[] weights = new int[length];
        // 总权重
        int totalWeight = 0;
        // 前一个Node的权重
        int lastWeight = -1;
        for (int i = 0; i < length; i++) {
            // 转化为WeightNode后获取权重
            int weight = getWeight((WeightNode<T>) nodes.get(i));
            // sum累加
            totalWeight += weight;
            // 存入weights数组
            weights[i] = totalWeight;
            // 出现了Node权重不一样的情况
            if (sameWeight && i > 0 && weight != lastWeight) {
                sameWeight = false;
            }
            // 更新lastWeight
            lastWeight = weight;
        }
        // 加权随机
        if (!sameWeight && totalWeight > 0) {
            // 随机生成一个[0, totalWeight)区间内的随机数
            int offset = ThreadLocalRandom.current().nextInt(totalWeight);
            for (int i = 0; i < length; i++) {
                // 选中第一个比offset大的
                if (offset < weights[i]) {
                    return nodes.get(i);
                }
            }
        }
        // all nodes have the same weight value. 退化成普通随机算法。
        return nodes.get(ThreadLocalRandom.current().nextInt(length));
    }
}
