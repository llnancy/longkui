package com.sunchaser.rpc.core.balancer.impl;

import com.sunchaser.rpc.core.balancer.Invoker;

import java.util.List;

/**
 * LFU
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/15
 */
public class LeastFrequentlyUsedLoadBalancer extends AbstractLoadBalancer {

    @Override
    protected <T> Invoker<T> doSelect(List<Invoker<T>> invokers, String routeKey) {
        return null;
    }
}
