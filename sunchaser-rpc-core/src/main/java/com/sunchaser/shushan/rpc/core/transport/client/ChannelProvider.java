package com.sunchaser.shushan.rpc.core.transport.client;

import com.google.common.collect.Maps;
import io.netty.channel.Channel;

import java.util.Objects;
import java.util.concurrent.ConcurrentMap;

/**
 * netty channel pool
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/13
 */
public class ChannelProvider {

    private static final ConcurrentMap<String, Channel> CHANNEL_POOL = Maps.newConcurrentMap();

    public static void putChannel(String key, Channel channel) {
        CHANNEL_POOL.put(key, channel);
    }

    public static Channel getChannel(String key) {
        Channel channel = CHANNEL_POOL.get(key);
        if (Objects.nonNull(channel)) {
            if (channel.isActive()) {
                return channel;
            } else {
                CHANNEL_POOL.remove(key);
            }
        }
        return null;
    }

    public static void removeChannel(String key) {
        CHANNEL_POOL.remove(key);
    }
}
