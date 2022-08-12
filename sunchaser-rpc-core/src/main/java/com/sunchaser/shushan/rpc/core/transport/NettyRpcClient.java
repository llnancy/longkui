package com.sunchaser.shushan.rpc.core.transport;

import com.sunchaser.shushan.rpc.core.codec.RpcCodec;
import com.sunchaser.shushan.rpc.core.handler.RpcResponseHandler;
import com.sunchaser.shushan.rpc.core.protocol.RpcProtocol;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Rpc client based on netty
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/15
 */
@Slf4j
public class NettyRpcClient<T> extends AbstractRpcClient<T> {

    private final Bootstrap bootstrap;

    private final EventLoopGroup eventLoopGroup;

    private volatile Channel channel;

    public static final int DEFAULT_CONNECTION_TIMEOUT = 3000;

    private static final int DEFAULT_IO_THREADS = Math.min(Runtime.getRuntime().availableProcessors() + 1, 32);

    public NettyRpcClient() {
        this(DEFAULT_CONNECTION_TIMEOUT);
    }

    public NettyRpcClient(Integer connectionTimeout) {
        this(connectionTimeout, DEFAULT_IO_THREADS);
    }

    public NettyRpcClient(Integer connectionTimeout, int nThreads) {
        super(connectionTimeout);
        this.bootstrap = new Bootstrap();
        this.eventLoopGroup = NettyEventLoopFactory.eventLoopGroup(nThreads, "NettyClientWorker");
        initBootstrap();
    }

    private void initBootstrap() {
        bootstrap.group(eventLoopGroup)
                .channel(NettyEventLoopFactory.socketChannelClass())
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, Math.max(DEFAULT_CONNECTION_TIMEOUT, connectionTimeout))
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast("rpc-codec", new RpcCodec<>())
                                .addLast("rpc-client-handler", new RpcResponseHandler());
                    }
                });
    }

    public void connect(String host, Integer port) {
        try {
            ChannelFuture future = bootstrap.connect(host, port);
            boolean notTimeout = future.awaitUninterruptibly(connectionTimeout, TimeUnit.MILLISECONDS);
            if (!notTimeout) {
                LOGGER.error("Rpc Netty client connect remote address[{}:{}] with timeout of {}ms", host, port, connectionTimeout);
            }
            if (notTimeout && future.isSuccess()) {
                Channel newChannel = future.channel();
                try {
                    // close old channel
                    Channel oldChannel = NettyRpcClient.this.channel;
                    if (Objects.nonNull(oldChannel)) {
                        LOGGER.info("Close old netty channel " + oldChannel + " on create new netty channel " + newChannel);
                        oldChannel.close();
                        // todo remove channel cache
                    }
                } finally {
                    NettyRpcClient.this.channel = newChannel;
                }
            }
            if (Objects.nonNull(channel) && channel.isActive()) {
                LOGGER.info("Rpc netty client started. {}", channel);
            }
            Throwable cause = future.cause();
            if (Objects.nonNull(cause)) {
                LOGGER.error("Rpc netty client failed to connect server [{}:{}]", host, port, cause);
            }
        } catch (Exception e) {
            LOGGER.error("Rpc netty client failed to connect server [{}:{}] with timeout of {}ms", host, port, connectionTimeout, e);
        }
    }

    @Override
    public void invoke(RpcProtocol<T> rpcProtocol) {
        channel.writeAndFlush(rpcProtocol)
                .addListener(promise -> {
                    if (!promise.isSuccess()) {
                        LOGGER.error("Rpc netty client channel writeAndFlush error.", promise.cause());
                    }
                });
    }
}
