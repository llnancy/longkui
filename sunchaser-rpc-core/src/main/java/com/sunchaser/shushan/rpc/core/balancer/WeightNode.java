package com.sunchaser.shushan.rpc.core.balancer;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Weightable Node
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/8/31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class WeightNode<T> extends Node<T> implements Weightable {

    /**
     * 节点权重，必须大于0，默认为1
     */
    private int weight = DEFAULT_WEIGHT;

    /**
     * 节点预热时间，默认预热10分钟
     */
    private int warmup = DEFAULT_WARMUP;
}
