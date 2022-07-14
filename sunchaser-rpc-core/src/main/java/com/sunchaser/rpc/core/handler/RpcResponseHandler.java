package com.sunchaser.rpc.core.handler;

import com.google.common.collect.Maps;
import com.sunchaser.rpc.core.protocol.RpcFuture;
import com.sunchaser.rpc.core.protocol.RpcHeader;
import com.sunchaser.rpc.core.protocol.RpcMessage;
import com.sunchaser.rpc.core.protocol.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/14
 */
public class RpcResponseHandler extends SimpleChannelInboundHandler<RpcMessage<RpcResponse>> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcMessage<RpcResponse> msg) throws Exception {
        RpcHeader rpcHeader = msg.getRpcHeader();
        long messageId = rpcHeader.getMessageId();
        RpcFuture<RpcResponse> rpcFuture = RpcResponseHolder.RPC_FUTURE_MAP.remove(messageId);
        // todo heartbeat
        rpcFuture.getPromise().setSuccess(msg.getContent());
    }

    static class RpcResponseHolder {

        private static final AtomicLong ID = new AtomicLong(0);

        private static final Map<Long, RpcFuture<RpcResponse>> RPC_FUTURE_MAP = Maps.newHashMap();
    }
}
