package com.sunchaser.shushan.rpc.core.handler;

import com.sunchaser.shushan.rpc.core.common.RpcContext;
import com.sunchaser.shushan.rpc.core.common.RpcMessageTypeEnum;
import com.sunchaser.shushan.rpc.core.exceptions.RpcException;
import com.sunchaser.shushan.rpc.core.protocol.RpcHeader;
import com.sunchaser.shushan.rpc.core.protocol.RpcProtocol;
import com.sunchaser.shushan.rpc.core.protocol.RpcRequest;
import com.sunchaser.shushan.rpc.core.protocol.RpcResponse;
import com.sunchaser.shushan.rpc.core.util.BeanFactory;
import com.sunchaser.shushan.rpc.core.util.ThrowableUtils;
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
public class RpcRequestHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcRequest>> {

    private final ThreadPoolExecutor requestHandlerPool;

    public RpcRequestHandler(ThreadPoolExecutor requestHandlerPool) {
        this.requestHandlerPool = requestHandlerPool;
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

        if (Objects.nonNull(requestHandlerPool)) {
            // 业务线程池中执行
            requestHandlerPool.execute(rpc);
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

    private Object doInvoke(RpcRequest rpcRequest) throws Exception {
        String serviceName = rpcRequest.getServiceName();
        Object bean = BeanFactory.getBean(serviceName);
        if (Objects.isNull(bean)) {
            throw new RpcException(serviceName + " service bean does not exist.");
        }
        String methodName = rpcRequest.getMethodName();
        Class<?>[] argTypes = rpcRequest.getArgTypes();
        Object[] args = rpcRequest.getArgs();

        // todo FastClass
        Method method = bean.getClass().getMethod(methodName, argTypes);
        method.setAccessible(true);
        return method.invoke(bean, args);
    }

}
