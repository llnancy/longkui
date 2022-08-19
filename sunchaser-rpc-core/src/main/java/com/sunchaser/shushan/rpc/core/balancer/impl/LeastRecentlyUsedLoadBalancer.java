package com.sunchaser.shushan.rpc.core.balancer.impl;

import com.sunchaser.shushan.rpc.core.balancer.Node;

import java.util.List;

/**
 * LRU
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/15
 */
public class LeastRecentlyUsedLoadBalancer extends AbstractLoadBalancer {

    @Override
    protected <T> Node<T> doSelect(List<Node<T>> nodes, String routeKey) {
        return null;
    }
}
