/*
 * Copyright 2023 LongKui
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Copyright 2022 LongKui
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

package io.github.llnancy.longkui.core.handler;

import io.github.llnancy.longkui.core.call.RpcCallback;
import io.github.llnancy.longkui.core.call.RpcCallbackExecutor;
import io.github.llnancy.longkui.core.common.Constants;
import io.github.llnancy.longkui.core.common.RpcContext;
import io.github.llnancy.longkui.core.common.RpcMessageTypeEnum;
import io.github.llnancy.longkui.core.exceptions.RpcException;
import io.github.llnancy.longkui.core.extension.ExtensionLoader;
import io.github.llnancy.longkui.core.protocol.RpcFuture;
import io.github.llnancy.longkui.core.protocol.RpcHeader;
import io.github.llnancy.longkui.core.protocol.RpcProtocol;
import io.github.llnancy.longkui.core.protocol.RpcResponse;
import io.github.llnancy.longkui.core.transport.client.ChannelContainer;
import io.github.llnancy.longkui.core.uid.SequenceIdGenerator;
import io.github.llnancy.longkui.core.uid.SequenceIdGeneratorEnum;
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
                LOGGER.debug("*********** LongKui rpc netty RpcResponseHandler read heartbeat pong. sequenceId={}", sequenceId);
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
