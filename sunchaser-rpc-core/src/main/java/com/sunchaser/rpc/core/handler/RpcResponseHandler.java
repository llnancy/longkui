package com.sunchaser.rpc.core.handler;

import com.sunchaser.rpc.core.protocol.RpcFuture;
import com.sunchaser.rpc.core.protocol.RpcHeader;
import com.sunchaser.rpc.core.protocol.RpcProtocol;
import com.sunchaser.rpc.core.protocol.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Rpc Response Handler
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/14
 */
public class RpcResponseHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcResponse>> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcProtocol<RpcResponse> msg) throws Exception {
        RpcHeader rpcHeader = msg.getRpcHeader();
        long sequenceId = rpcHeader.getSequenceId();
        RpcFuture<RpcResponse> rpcFuture = RpcResponseHolder.removeRpcFuture(sequenceId);
        // todo heartbeat
        rpcFuture.getPromise().setSuccess(msg.getContent());
    }
}
