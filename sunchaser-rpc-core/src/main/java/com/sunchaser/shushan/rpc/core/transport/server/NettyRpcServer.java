package com.sunchaser.shushan.rpc.core.transport.server;

import com.sunchaser.shushan.rpc.core.codec.RpcCodec;
import com.sunchaser.shushan.rpc.core.common.Constants;
import com.sunchaser.shushan.rpc.core.handler.RpcRequestHandler;
import com.sunchaser.shushan.rpc.core.transport.NettyEventLoopFactory;
import com.sunchaser.shushan.rpc.core.util.ThreadPools;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.net.InetSocketAddress;

/**
 * a rpc server implementation based on Netty
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/15
 */
public class NettyRpcServer implements RpcServer {

    private final ServerBootstrap bootstrap;

    public NettyRpcServer() {
        this(Constants.DEFAULT_IO_THREADS);
    }

    public NettyRpcServer(int nThreads) {
        EventLoopGroup bossGroup = NettyEventLoopFactory.eventLoopGroup(1, "NettyServerBoss");
        EventLoopGroup workerGroup = NettyEventLoopFactory.eventLoopGroup(nThreads, "NettyServerWorker");
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
                                .addLast("rpc-server-idle-state-handler", new IdleStateHandler(60, 0, 0))
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

    @Override
    public void start(InetSocketAddress localAddress) {
        ChannelFuture channelFuture = bootstrap.bind(localAddress);
        channelFuture.syncUninterruptibly();
    }
}