package com.sunchaser.rpc.core.transport;

import com.google.common.collect.Maps;
import io.netty.channel.Channel;

import java.util.concurrent.ConcurrentMap;

/**
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/15
 */
public class NettyChannel {

    private static final ConcurrentMap<String, Channel> CHANNEL_MAP = Maps.newConcurrentMap();
}
