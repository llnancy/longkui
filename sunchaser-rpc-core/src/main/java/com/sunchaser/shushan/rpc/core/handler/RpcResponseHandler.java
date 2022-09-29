/*
 * Copyright 2022 SunChaser
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

package com.sunchaser.shushan.rpc.core.handler;

import com.sunchaser.shushan.rpc.core.call.RpcCallback;
import com.sunchaser.shushan.rpc.core.call.RpcCallbackExecutor;
import com.sunchaser.shushan.rpc.core.common.Constants;
import com.sunchaser.shushan.rpc.core.common.RpcContext;
import com.sunchaser.shushan.rpc.core.common.RpcMessageTypeEnum;
import com.sunchaser.shushan.rpc.core.exceptions.RpcException;
import com.sunchaser.shushan.rpc.core.extension.ExtensionLoader;
import com.sunchaser.shushan.rpc.core.protocol.RpcFuture;
import com.sunchaser.shushan.rpc.core.protocol.RpcHeader;
import com.sunchaser.shushan.rpc.core.protocol.RpcProtocol;
import com.sunchaser.shushan.rpc.core.protocol.RpcResponse;
import com.sunchaser.shushan.rpc.core.transport.client.ChannelContainer;
import com.sunchaser.shushan.rpc.core.uid.SequenceIdGenerator;
import com.sunchaser.shushan.rpc.core.uid.SequenceIdGeneratorEnum;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.net.SocketAddress;
import java.util.Objects;

/**
 * Rpc Response Handler
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/14
 */
@Slf4j
@ChannelHandler.Sharable
public class RpcResponseHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcResponse>> {

    private final RpcCallbackExecutor rpcCallbackExecutor;

    public RpcResponseHandler(RpcCallbackExecutor rpcCallbackExecutor) {
        this.rpcCallbackExecutor = rpcCallbackExecutor;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcProtocol<RpcResponse> msg) throws Exception {
        RpcHeader rpcHeader = msg.getRpcHeader();
        long sequenceId = rpcHeader.getSequenceId();
        byte type = rpcHeader.getType();
        if (RpcMessageTypeEnum.isHeartbeat(type)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("*********** sunchaser-rpc netty RpcResponseHandler read heartbeat pong. sequenceId={}", sequenceId);
            }
            return;
        }
        RpcFuture<RpcResponse> rpcFuture = RpcPendingHolder.removeRpcFuture(sequenceId);
        if (Objects.isNull(rpcFuture)) {
            return;
        }
        RpcResponse rpcResponse = msg.getRpcBody();
        @SuppressWarnings("unchecked")
        RpcCallback<Object> rpcCallback = (RpcCallback<Object>) rpcFuture.getRpcCallback();
        if (Objects.nonNull(rpcCallback)) {
            // callback type
            rpcCallbackExecutor.execute(() -> {
                String errorMsg = rpcResponse.getErrorMsg();
                if (StringUtils.isNotBlank(errorMsg)) {
                    rpcCallback.onError(new RpcException(errorMsg));
                } else {
                    rpcCallback.onSuccess(rpcResponse.getResult());
                }
            });
        } else {
            // other normal call type
            rpcFuture.getPromise().setSuccess(msg.getRpcBody());
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {
                // 触发写超时事件
                sendPingHeartBeat(ctx);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    /**
     * heart bean: send ping
     *
     * @param ctx ChannelHandlerContext
     */
    private static void sendPingHeartBeat(ChannelHandlerContext ctx) {
        SocketAddress remoteAddress = ctx.channel().remoteAddress();
        LOGGER.info("[{}] triggered write idle event", remoteAddress);
        SequenceIdGenerator sequenceIdGenerator = ExtensionLoader.getExtensionLoader(SequenceIdGenerator.class)
                .getExtension(SequenceIdGeneratorEnum.ATOMIC_LONG);
        long sequenceId = sequenceIdGenerator.nextSequenceId();
        RpcHeader rpcHeader = RpcHeader.builder()
                .magic(RpcContext.MAGIC)
                .version(Constants.DEFAULT_PROTOCOL_VERSION)
                .type(RpcMessageTypeEnum.HEARTBEAT.getCode())
                .serialize(Constants.DEFAULT_SERIALIZE)
                .compress(Constants.DEFAULT_COMPRESS)
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
                ChannelContainer.removeChannel(remoteAddress.toString());
            }
        });
    }
}
