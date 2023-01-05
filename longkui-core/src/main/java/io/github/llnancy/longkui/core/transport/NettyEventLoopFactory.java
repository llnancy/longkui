/*
 * Copyright 2022 LongKui
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.llnancy.longkui.core.transport;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;

/**
 * adapter EventLoopGroup and SocketChannel for different platforms
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/8/12
 */
public class NettyEventLoopFactory {

    public static EventLoopGroup eventLoopGroup(int nThreads, String threadFactoryName) {
        DefaultThreadFactory threadFactory = new DefaultThreadFactory(threadFactoryName, true);
        return shouldEpoll() ? new EpollEventLoopGroup(nThreads, threadFactory) : new NioEventLoopGroup(nThreads, threadFactory);
    }

    public static Class<? extends SocketChannel> socketChannelClass() {
        return shouldEpoll() ? EpollSocketChannel.class : NioSocketChannel.class;
    }

    public static Class<? extends ServerSocketChannel> serverSocketChannelClass() {
        return shouldEpoll() ? EpollServerSocketChannel.class : NioServerSocketChannel.class;
    }

    private static boolean shouldEpoll() {
        return System.getProperty("os.name").toLowerCase().contains("linux") && Epoll.isAvailable();
    }
}
