package com.sunchaser.rpc.core.transport;

import com.sunchaser.rpc.core.codec.RpcCodec;
import com.sunchaser.rpc.core.handler.RpcResponseHandler;
import com.sunchaser.rpc.core.protocol.RpcProtocol;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
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

    public NettyRpcClient(String host, Integer port) {
        this(host, port, 3000);
    }

    public NettyRpcClient(String host, Integer port, Integer connectionTimeout) {
        this(host, port, connectionTimeout, 0);
    }

    public NettyRpcClient(String host, Integer port, Integer connectionTimeout, int nThreads) {
        super(host, port, connectionTimeout);
        this.bootstrap = new Bootstrap();
        this.eventLoopGroup = new NioEventLoopGroup(nThreads);
        configBootstrap();
    }

    private void configBootstrap() {
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeout)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)// 避免tcp nagle算法的发送延迟
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast("rpc-codec", new RpcCodec<>())
                                .addLast("rpc-client-handler", new RpcResponseHandler());
                    }
                });
    }

    public boolean connect() {
        try {
            ChannelFuture future = bootstrap.connect(host, port);
            boolean notTimeout = future.awaitUninterruptibly(connectionTimeout, TimeUnit.MILLISECONDS);
            if (!notTimeout) {
                log.error("Rpc Netty client connect remote address[{}:{}] with timeout of {}ms", host, port, connectionTimeout);
            }
            if (notTimeout && future.isSuccess()) {
                Channel newChannel = future.channel();
                try {
                    // close old channel
                    Channel oldChannel = NettyRpcClient.this.channel;
                    if (Objects.nonNull(oldChannel)) {
                        log.info("Close old netty channel " + oldChannel + " on create new netty channel " + newChannel);
                        oldChannel.close();
                        // todo remove channel cache
                    }
                } finally {
                    NettyRpcClient.this.channel = newChannel;
                }
            }
            if (Objects.nonNull(channel) && channel.isActive()) {
                log.info("Rpc netty client started. {}", channel);
                return true;
            }
            Throwable cause = future.cause();
            if (Objects.nonNull(cause)) {
                log.error("Rpc netty client failed to connect server [{}:{}]", host, port, cause);
            }
        } catch (Exception e) {
            log.error("Rpc netty client failed to connect server [{}:{}] with timeout of {}ms", host, port, connectionTimeout, e);
        }
        return false;
    }

    @Override
    public void invoke(RpcProtocol<T> rpcProtocol) {
        channel.writeAndFlush(rpcProtocol).addListener(promise -> {
            if (!promise.isSuccess()) {
                log.error("Rpc netty client channel writeAndFlush error.", promise.cause());
            }
        });
    }
}
