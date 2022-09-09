package com.sunchaser.shushan.rpc.core.balancer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Node
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Node<T> {

    /**
     * 节点
     */
    private T node;

    /**
     * 时间戳
     */
    private long timestamp;
}
