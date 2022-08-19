package com.sunchaser.shushan.rpc.core.balancer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Node
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Node<T> {

    /**
     * 节点
     */
    private T node;

    /**
     * 节点权重，必须大于0，默认为1
     */
    private int weight = 1;
}
