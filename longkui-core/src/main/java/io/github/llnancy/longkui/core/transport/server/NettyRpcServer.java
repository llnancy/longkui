/*
 * Copyright 2022 LongKui
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.llnancy.longkui.core.transport.server;

import io.github.llnancy.longkui.core.config.RpcServerConfig;
import io.github.llnancy.longkui.core.config.RpcShutdownHook;
import io.github.llnancy.longkui.core.handler.RpcRequestHandler;
import io.github.llnancy.longkui.core.transport.NettyEventLoopFactory;
import io.github.llnancy.longkui.core.transport.codec.RpcCodec;
import io.github.llnancy.longkui.core.util.ThreadPools;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

/**
 * a rpc server implementation based on Netty
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/15
 */
@Slf4j
public class NettyRpcServer implements RpcServer {

    private final ServerBootstrap bootstrap;

    private final EventLoopGroup bossGroup;

    private final EventLoopGroup workerGroup;

    private final ExecutorService requestExecutor;

    public NettyRpcServer() {
        this(RpcServerConfig.createDefaultConfig());
    }

    public NettyRpcServer(RpcServerConfig rpcServerConfig) {
        // 注册JVM钩子进行资源优雅关闭
        Runtime.getRuntime().addShutdownHook(RpcShutdownHook.getRpcShutdownHook());
        bossGroup = NettyEventLoopFactory.eventLoopGroup(1, "NettyServerBoss");
        workerGroup = NettyEventLoopFactory.eventLoopGroup(rpcServerConfig.getIoThreads(), "NettyServerWorker");
        this.requestExecutor = ThreadPools.createThreadPoolIfAbsent(
                rpcServerConfig.getRequestExecutorConfig()
                        .setThreadNameIdentifier(this.getClass().getName())
        );
        this.bootstrap = new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(NettyEventLoopFactory.serverSocketChannelClass())
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {

                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast("rpc-codec", new RpcCodec<>())
                                .addLast("rpc-server-idle-state-handler", new IdleStateHandler(RpcServerConfig.DEFAULT_READER_IDLE_TIME_SECONDS, 0, 0))
                                .addLast("rpc-server-handler", new RpcRequestHandler(requestExecutor));
                    }
                });
    }

    @Override
    public void start(InetSocketAddress localAddress) {
        try {
            ChannelFuture channelFuture = bootstrap.bind(localAddress)
                    .syncUninterruptibly();
            LOGGER.info("sunchaser-rpc >>>>>> server initialized with port(s): {} (tcp)", localAddress.getPort());
            channelFuture.channel()
                    .closeFuture()
                    .syncUninterruptibly();
        } catch (Throwable t) {
            LOGGER.error("sunchaser-rpc >>>>>> server initialize error", t);
        } finally {
            destroy();
        }
    }

    @Override
    public void destroy() {
        if (!bossGroup.isShutdown()) {
            bossGroup.shutdownGracefully();
        }
        if (!workerGroup.isShutdown()) {
            workerGroup.shutdownGracefully();
        }
        if (Objects.nonNull(requestExecutor)) {
            ThreadPools.shutdown(requestExecutor);
        }
    }
}
