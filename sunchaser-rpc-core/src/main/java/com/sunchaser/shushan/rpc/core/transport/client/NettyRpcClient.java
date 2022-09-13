package com.sunchaser.shushan.rpc.core.transport.client;

import com.sunchaser.shushan.rpc.core.codec.RpcCodec;
import com.sunchaser.shushan.rpc.core.common.Constants;
import com.sunchaser.shushan.rpc.core.exceptions.RpcException;
import com.sunchaser.shushan.rpc.core.handler.RpcResponseHandler;
import com.sunchaser.shushan.rpc.core.protocol.RpcProtocol;
import com.sunchaser.shushan.rpc.core.protocol.RpcRequest;
import com.sunchaser.shushan.rpc.core.transport.NettyEventLoopFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * a rpc client implementation based on Netty
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/15
 */
@Slf4j
public class NettyRpcClient extends AbstractRpcClient {

    private static final RpcClient INSTANCE = new NettyRpcClient();

    public static RpcClient getInstance() {
        return INSTANCE;
    }

    private final Bootstrap bootstrap;

    public NettyRpcClient() {
        this(Constants.DEFAULT_CONNECTION_TIMEOUT);
    }

    public NettyRpcClient(Integer connectionTimeout) {
        this(connectionTimeout, Constants.DEFAULT_IO_THREADS);
    }

    public NettyRpcClient(Integer connectionTimeout, int nThreads) {
        super(connectionTimeout);
        this.bootstrap = new Bootstrap();
        initBootstrap(nThreads);
    }

    private void initBootstrap(int nThreads) {
        bootstrap.group(NettyEventLoopFactory.eventLoopGroup(nThreads, "NettyClientWorker"))
                .channel(NettyEventLoopFactory.socketChannelClass())
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, Math.max(Constants.DEFAULT_CONNECTION_TIMEOUT, connectionTimeout))
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast("rpc-codec", new RpcCodec<>())
                                .addLast("rpc-client-idle-state-handler", new IdleStateHandler(0, 30, 0))
                                .addLast("rpc-client-handler", new RpcResponseHandler());
                    }
                });
    }

    @Override
    public void invoke(RpcProtocol<RpcRequest> rpcProtocol, InetSocketAddress localAddress) {
        String key = localAddress.toString();
        Channel channel = ChannelProvider.getChannel(key);
        if (Objects.isNull(channel)) {
            channel = connect(localAddress);
            ChannelProvider.putChannel(key, channel);
        }
        if (channel.isActive()) {
            channel.writeAndFlush(rpcProtocol)
                    .addListener((ChannelFutureListener) future -> {
                        if (!future.isSuccess()) {
                            LOGGER.error("Rpc netty client channel writeAndFlush error.", future.cause());
                            future.channel().close();
                            ChannelProvider.removeChannel(key);
                        }
                    });
        }
    }

    public Channel connect(InetSocketAddress connectAddress) {
        Channel channel = null;
        ChannelFuture future = bootstrap.connect(connectAddress);
        boolean notTimeout = future.awaitUninterruptibly(connectionTimeout, TimeUnit.MILLISECONDS);
        if (!notTimeout) {
            LOGGER.error("Rpc Netty client connect remote address[{}] with timeout of {}ms", connectAddress, connectionTimeout);
        }
        if (notTimeout && future.isSuccess()) {
            channel = future.channel();
        }
        if (Objects.nonNull(channel) && channel.isActive()) {
            LOGGER.info("Rpc netty client started. {}", channel);
        }
        Throwable cause = future.cause();
        if (Objects.nonNull(cause)) {
            throw new RpcException("Rpc netty client failed to connect server [" + connectAddress + "], error message is:" + future.cause().getMessage(), cause);
        }
        return channel;
    }
}