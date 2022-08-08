package com.sunchaser.shushan.rpc.core.transport;

import com.sunchaser.shushan.rpc.core.codec.RpcCodec;
import com.sunchaser.shushan.rpc.core.handler.RpcRequestHandler;
import com.sunchaser.shushan.rpc.core.util.ThreadPools;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Rpc Server
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/15
 */
public class RpcServer {

    private final ServerBootstrap bootstrap;

    private final int port;

    public static final int DEFAULT_PORT = 1234;

    public RpcServer() {
        this(DEFAULT_PORT);
    }

    public RpcServer(int port) {
        this.port = port;
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        this.bootstrap = new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {

                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast("rpc-codec", new RpcCodec<>())
                                .addLast(
                                        "rpc-server-handler",
                                        new RpcRequestHandler(
                                                ThreadPools.createThreadPool(
                                                        this.getClass().getName(),
                                                        10,
                                                        10
                                                )
                                        )
                                );
                    }
                });
    }

    public void start() {
        bootstrap.bind(port);
    }
}
