package com.sunchaser.rpc.core.balancer.impl;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 随机
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/15
 */
public class RandomLoadBalancer<T> extends AbstractLoadBalancer<T> {

    private final ThreadLocalRandom random = ThreadLocalRandom.current();

    @Override
    protected T doSelect(List<T> servers) {
        return servers.get(random.nextInt(servers.size()));
    }
}
