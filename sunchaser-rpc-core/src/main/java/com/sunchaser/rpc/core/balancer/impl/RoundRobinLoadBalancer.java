package com.sunchaser.rpc.core.balancer.impl;

import java.util.List;

/**
 * 普通轮询
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/15
 */
public class RoundRobinLoadBalancer<T> extends AbstractLoadBalancer<T> {

    private Integer index = 0;

    @Override
    protected T doSelect(List<T> servers) {
        T server;
        synchronized (this) {
            if (index >= servers.size()) {
                // 重置
                index = 0;
            }
            server = servers.get(index);
            index++;
        }
        return server;
    }
}
