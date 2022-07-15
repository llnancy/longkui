package com.sunchaser.rpc.core.balancer.impl;

import java.util.List;

/**
 * 一致性哈希
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/15
 */
public class ConsistentHashLoadBalancer<T> extends AbstractLoadBalancer<T> {

    @Override
    protected T doSelect(List<T> servers) {
        return null;
    }
}
