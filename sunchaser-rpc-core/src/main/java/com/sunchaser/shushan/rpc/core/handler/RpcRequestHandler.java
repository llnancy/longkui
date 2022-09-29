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

import com.sunchaser.shushan.rpc.core.common.RpcContext;
import com.sunchaser.shushan.rpc.core.common.RpcMessageTypeEnum;
import com.sunchaser.shushan.rpc.core.protocol.RpcHeader;
import com.sunchaser.shushan.rpc.core.protocol.RpcProtocol;
import com.sunchaser.shushan.rpc.core.protocol.RpcRequest;
import com.sunchaser.shushan.rpc.core.protocol.RpcResponse;
import com.sunchaser.shushan.rpc.core.provider.ServiceProvider;
import com.sunchaser.shushan.rpc.core.provider.impl.InMemoryServiceProvider;
import com.sunchaser.shushan.rpc.core.util.ThrowableUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

/**
 * Netty Rpc Request Handler
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/13
 */
@Slf4j
public class RpcRequestHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcRequest>> {

    private final ExecutorService requestExecutor;

    private final ServiceProvider serviceProvider;

    public RpcRequestHandler(ExecutorService requestExecutor) {
        this.requestExecutor = requestExecutor;
        this.serviceProvider = InMemoryServiceProvider.getInstance();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcProtocol<RpcRequest> msg) throws Exception {
        RpcHeader rpcHeader = msg.getRpcHeader();
        long sequenceId = rpcHeader.getSequenceId();
        byte type = rpcHeader.getType();

        if (RpcMessageTypeEnum.isHeartbeat(type)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("*********** sunchaser-rpc netty RpcRequestHandler read heartbeat ping. sequenceId={}", sequenceId);
            }
            RpcProtocol<String> pong = RpcProtocol.<String>builder()
                    .rpcHeader(rpcHeader)
                    .rpcBody(RpcContext.PONG)
                    .build();
            ctx.writeAndFlush(pong);
            return;
        }

        Runnable rpc = () -> {
            // 将消息类型从请求转为响应
            rpcHeader.setType(RpcMessageTypeEnum.RESPONSE.getCode());
            // 构建响应对象
            RpcProtocol<RpcResponse> rpcProtocol = RpcProtocol.<RpcResponse>builder()
                    .rpcHeader(rpcHeader)
                    .build();
            RpcResponse rpcResponse = null;
            try {
                // 反射调用
                RpcRequest rpcRequest = msg.getRpcBody();
                rpcResponse = invokeService(rpcRequest);
            } catch (Exception e) {
                rpcResponse = handleException(sequenceId, e);
            } finally {
                rpcProtocol.setRpcBody(rpcResponse);
                ctx.writeAndFlush(rpcProtocol);
            }
        };

        if (Objects.nonNull(requestExecutor)) {
            // 业务线程池中执行
            requestExecutor.execute(rpc);
        } else {
            // 业务线程池未被定义，直接在IO线程执行
            rpc.run();
        }
    }

    private RpcResponse handleException(long sequenceId, Exception e) {
        LOGGER.error("process request sequenceId[{}] error", sequenceId, e);
        return RpcResponse.builder()
                .errorMsg(ThrowableUtils.toString(e))
                .build();
    }

    private RpcResponse invokeService(RpcRequest rpcRequest) throws Exception {
        Object result = doInvoke(rpcRequest);
        return RpcResponse.builder()
                .result(result)
                .build();
    }

    /**
     * do invoke
     *
     * @param rpcRequest RpcRequest
     * @return invoke result
     * @throws Exception invoke Exception
     */
    private Object doInvoke(RpcRequest rpcRequest) throws Exception {
        String serviceKey = rpcRequest.getRpcServiceKey();
        Object bean = serviceProvider.getProvider(serviceKey);
        String methodName = rpcRequest.getMethodName();
        Class<?>[] argTypes = rpcRequest.getArgTypes();
        Object[] args = rpcRequest.getArgs();

        // todo FastClass
        Method method = bean.getClass().getMethod(methodName, argTypes);
        method.setAccessible(true);
        return method.invoke(bean, args);
    }

}
