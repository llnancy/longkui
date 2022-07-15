package com.sunchaser.rpc.core.balancer.impl;

import java.util.List;

/**
 * 随机
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/15
 */
public class RandomLoadBalancer<T> extends AbstractLoadBalancer<T> {

    @Override
    protected T doSelect(List<T> servers) {
        return null;
    }
}
