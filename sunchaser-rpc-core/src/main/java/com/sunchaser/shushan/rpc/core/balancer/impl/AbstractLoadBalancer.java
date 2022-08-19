package com.sunchaser.shushan.rpc.core.balancer.impl;

import com.sunchaser.shushan.rpc.core.balancer.Node;
import com.sunchaser.shushan.rpc.core.balancer.LoadBalancer;
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
    public <T> Node<T> select(List<Node<T>> nodes, String routeKey) {
        if (CollectionUtils.isEmpty(nodes)) {
            return null;
        }
        if (nodes.size() == 1) {
            return nodes.get(0);
        }
        return doSelect(nodes, routeKey);
    }

    protected abstract <T> Node<T> doSelect(List<Node<T>> nodes, String routeKey);
}
