package com.sunchaser.shushan.rpc.core.balancer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
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
public class WeightNode<T> extends Node<T> implements Weightable {

    public static final int DEFAULT_WEIGHT = 1;

    /**
     * 节点权重，必须大于0，默认为1
     */
    private int weight = DEFAULT_WEIGHT;
}
