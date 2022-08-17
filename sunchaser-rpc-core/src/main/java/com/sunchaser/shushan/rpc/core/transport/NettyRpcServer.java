package com.sunchaser.shushan.rpc.core.transport;

import com.sunchaser.shushan.rpc.core.codec.RpcCodec;
import com.sunchaser.shushan.rpc.core.common.Constants;
import com.sunchaser.shushan.rpc.core.handler.RpcRequestHandler;
import com.sunchaser.shushan.rpc.core.util.ThreadPools;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.SneakyThrows;

import java.net.InetSocketAddress;
import java.util.Objects;

/**
 * a rpc server implementation based on Netty
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/15
 */
public class NettyRpcServer implements RpcServer {

    private final ServerBootstrap bootstrap;

    private final EventLoopGroup bossGroup;

    private final EventLoopGroup workerGroup;

    public NettyRpcServer() {
        this(Constants.DEFAULT_IO_THREADS);
    }

    public NettyRpcServer(int nThreads) {
        this.bossGroup = NettyEventLoopFactory.eventLoopGroup(1, "NettyServerBoss");
        this.workerGroup = NettyEventLoopFactory.eventLoopGroup(nThreads, "NettyServerWorker");
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

    @SneakyThrows({Throwable.class, Exception.class})
    @Override
    public void start(InetSocketAddress localAddress) {
        try {
            ChannelFuture channelFuture = bootstrap.bind(localAddress);
            channelFuture.syncUninterruptibly();
            channelFuture.channel().closeFuture().sync();
        } finally {
            if (Objects.nonNull(bootstrap)) {
                bossGroup.shutdownGracefully().syncUninterruptibly();
                workerGroup.shutdownGracefully().syncUninterruptibly();
            }
        }
    }
}
