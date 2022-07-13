package com.sunchaser.rpc.core.handler;

import com.sunchaser.rpc.core.common.RpcContext;
import com.sunchaser.rpc.core.exceptions.RpcException;
import com.sunchaser.rpc.core.protocol.RpcHeader;
import com.sunchaser.rpc.core.protocol.RpcMessage;
import com.sunchaser.rpc.core.protocol.RpcRequest;
import com.sunchaser.rpc.core.protocol.RpcResponse;
import com.sunchaser.rpc.core.util.BeanFactory;
import com.sunchaser.rpc.core.util.ThrowableUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Netty Rpc Request Handler
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/13
 */
@Slf4j
public class RpcRequestHandler extends SimpleChannelInboundHandler<RpcMessage<RpcRequest>> {

    private final ThreadPoolExecutor requestHandlerPool;

    public RpcRequestHandler(ThreadPoolExecutor requestHandlerPool) {
        this.requestHandlerPool = requestHandlerPool;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcMessage<RpcRequest> msg) throws Exception {
        RpcHeader rpcHeader = msg.getRpcHeader();
        if (RpcContext.isHeartbeat(rpcHeader.getProtocolHeader())) {
            log.debug("*********** sunchaser-rpc netty RpcRequestHandler read heartbeat.");
            return;
        }

        // 将消息类型从请求转为响应
        rpcHeader.setProtocolHeader(RpcContext.transformToResponse(rpcHeader.getProtocolHeader()));

        requestHandlerPool.execute(() -> {
            try {
                RpcMessage<RpcResponse> rpcMessage = invokeService(msg);
                ctx.writeAndFlush(rpcMessage);
            } catch (Exception e) {
                handlerException(ctx, rpcHeader, e);
            }
        });
    }

    private void handlerException(ChannelHandlerContext ctx, RpcHeader rpcHeader, Exception e) {
        RpcMessage<RpcResponse> rpcMessage = RpcMessage.<RpcResponse>builder()
                .rpcHeader(rpcHeader)
                .content(RpcResponse.builder()
                        .errorMsg(ThrowableUtils.toString(e))
                        .build()
                )
                .build();
        ctx.writeAndFlush(rpcMessage);
    }

    private RpcMessage<RpcResponse> invokeService(RpcMessage<RpcRequest> msg) throws Exception {
        RpcHeader rpcHeader = msg.getRpcHeader();
        RpcResponse rpcResponse = new RpcResponse();
        RpcMessage<RpcResponse> rpcMessage = RpcMessage.<RpcResponse>builder()
                .rpcHeader(rpcHeader)
                .content(rpcResponse)
                .build();

        if (System.currentTimeMillis() - rpcHeader.getTs() > 3 * 60 * 1000) {
            rpcResponse.setErrorMsg("The timestamp difference between invoke and executor exceeds the limit - three minutes.");
            return rpcMessage;
        }

        RpcRequest rpcRequest = msg.getContent();
        Object result = doInvoke(rpcRequest);
        rpcResponse.setResult(result);
        return rpcMessage;
    }

    private Object doInvoke(RpcRequest rpcRequest) throws Exception {
        String serviceName = rpcRequest.getServiceName();
        Object bean = BeanFactory.getBean(serviceName);
        if (Objects.isNull(bean)) {
            throw new RpcException(serviceName + " service bean does not exist.");
        }
        String methodName = rpcRequest.getMethodName();
        Class<?>[] argTypes = rpcRequest.getArgTypes();
        Object[] args = rpcRequest.getArgs();
        Method method = bean.getClass().getMethod(methodName, argTypes);
        method.setAccessible(true);
        return method.invoke(bean, args);
    }

}
