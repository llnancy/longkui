package com.sunchaser.shushan.rpc.core.transport;

import com.google.common.collect.Maps;

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

    public void put(String address, C connection) {
        CONNECTION_POOL.putIfAbsent(address, connection);
    }

    public void put(InetSocketAddress connectAddress, C connection) {
        put(connectAddress.toString(), connection);
    }

    public C get(String address) {
        return CONNECTION_POOL.get(address);
    }

    public C get(InetSocketAddress connectAddress) {
        return get(connectAddress.toString());
    }

    public C remove(String address) {
        return CONNECTION_POOL.remove(address);
    }

    public C remove(InetSocketAddress connectAddress) {
        return remove(connectAddress.toString());
    }

}
