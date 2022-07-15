package com.sunchaser.rpc.core.balancer.impl;

import com.sunchaser.rpc.core.balancer.LoadBalancer;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * RPC调用 负载均衡策略接口抽象实现
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/15
 */
public abstract class AbstractLoadBalancer<T> implements LoadBalancer<T> {

    @Override
    public T select(List<T> servers) {
        if (CollectionUtils.isEmpty(servers)) {
            return null;
        }
        if (servers.size() == 1) {
            return servers.get(0);
        }
        return doSelect(servers);
    }

    protected abstract T doSelect(List<T> servers);
}
