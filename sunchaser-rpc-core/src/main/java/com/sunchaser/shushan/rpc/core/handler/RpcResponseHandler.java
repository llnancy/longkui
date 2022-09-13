package com.sunchaser.shushan.rpc.core.handler;

import com.sunchaser.shushan.rpc.core.common.RpcContext;
import com.sunchaser.shushan.rpc.core.protocol.RpcFuture;
import com.sunchaser.shushan.rpc.core.protocol.RpcHeader;
import com.sunchaser.shushan.rpc.core.protocol.RpcProtocol;
import com.sunchaser.shushan.rpc.core.protocol.RpcResponse;
import com.sunchaser.shushan.rpc.core.transport.client.ChannelProvider;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketAddress;

/**
 * Rpc Response Handler
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/14
 */
@Slf4j
@ChannelHandler.Sharable
public class RpcResponseHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcResponse>> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcProtocol<RpcResponse> msg) throws Exception {
        RpcHeader rpcHeader = msg.getRpcHeader();
        long sequenceId = rpcHeader.getSequenceId();
        byte versionAndType = rpcHeader.getVersionAndType();
        if (RpcContext.isHeartbeat(versionAndType)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("*********** sunchaser-rpc netty RpcResponseHandler read heartbeat pong. sequenceId={}", sequenceId);
            }
            return;
        }
        RpcFuture<RpcResponse> rpcFuture = RpcPendingHolder.removeRpcFuture(sequenceId);
        rpcFuture.getPromise().setSuccess(msg.getRpcBody());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {
                // 触发写超时事件
                SocketAddress remoteAddress = ctx.channel().remoteAddress();
                LOGGER.info("[{}] triggered write idle event", remoteAddress);
                long sequenceId = RpcPendingHolder.generateSequenceId();
                RpcHeader rpcHeader = RpcHeader.builder()
                        .magic(RpcContext.MAGIC)
                        .versionAndType(RpcContext.DEFAULT_VERSION_AND_HEARTBEAT_TYPE)
                        .compressAndSerialize(RpcContext.DEFAULT_COMPRESS_SERIALIZE)
                        .sequenceId(sequenceId)
                        .build();
                RpcProtocol<String> ping = RpcProtocol.<String>builder()
                        .rpcHeader(rpcHeader)
                        .rpcBody(RpcContext.PING)
                        .build();
                ctx.writeAndFlush(ping).addListener((ChannelFutureListener) future -> {
                    if (!future.isSuccess()) {
                        future.channel().close();
                        // 删除对应Channel
                        ChannelProvider.removeChannel(remoteAddress.toString());
                    }
                });
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
