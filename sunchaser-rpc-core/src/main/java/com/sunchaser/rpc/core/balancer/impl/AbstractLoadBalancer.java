package com.sunchaser.rpc.core.balancer.impl;

import com.sunchaser.rpc.core.balancer.Invoker;
import com.sunchaser.rpc.core.balancer.LoadBalancer;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * RPC调用 负载均衡策略接口抽象实现
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/15
 */
public abstract class AbstractLoadBalancer implements LoadBalancer {

    @Override
    public <T> Invoker<T> select(List<Invoker<T>> invokers, String routeKey) {
        if (CollectionUtils.isEmpty(invokers)) {
            return null;
        }
        if (invokers.size() == 1) {
            return invokers.get(0);
        }
        return doSelect(invokers, routeKey);
    }

    protected abstract <T> Invoker<T> doSelect(List<Invoker<T>> invokers, String routeKey);
}
