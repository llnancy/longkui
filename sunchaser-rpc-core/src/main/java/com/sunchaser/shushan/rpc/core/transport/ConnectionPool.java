package com.sunchaser.shushan.rpc.core.transport;

import com.google.common.collect.Maps;

import javax.annotation.Nonnull;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentMap;

/**
 * 连接对象池
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/15
 */
public class ConnectionPool<C> {

    private final ConcurrentMap<String, C> CONNECTION_POOL = Maps.newConcurrentMap();

    public void put(@Nonnull InetSocketAddress connectAddress, C connection) {
        CONNECTION_POOL.putIfAbsent(connectAddress.toString(), connection);
    }

    public C get(@Nonnull InetSocketAddress connectAddress) {
        return CONNECTION_POOL.get(connectAddress.toString());
    }

    public C remove(@Nonnull InetSocketAddress connectAddress) {
        return CONNECTION_POOL.remove(connectAddress.toString());
    }

}
