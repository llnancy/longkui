package com.sunchaser.shushan.rpc.core.test;

import com.sunchaser.shushan.rpc.core.common.RpcContext;
import com.sunchaser.shushan.rpc.core.exceptions.RpcException;
import com.sunchaser.shushan.rpc.core.handler.RpcPendingHolder;
import com.sunchaser.shushan.rpc.core.protocol.*;
import com.sunchaser.shushan.rpc.core.transport.NettyRpcClient;
import io.netty.channel.DefaultEventLoop;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * 点对点RPC服务消费者
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/8/18
 */
@Slf4j
public class P2PConsumer {

    public static void main(String[] args) throws Exception {
        // 构建协议头
        long sequenceId = RpcPendingHolder.generateSequenceId();
        RpcHeader rpcHeader = RpcHeader.builder()
                .magic(RpcContext.MAGIC)
                .versionAndType(RpcContext.DEFAULT_VERSION_AND_TYPE)
                .compressAndSerialize(RpcContext.DEFAULT_COMPRESS_SERIALIZE)
                .sequenceId(sequenceId)
                .build();

        // 构建协议体：调用HelloService的sayHello方法，传递参数hello
        String methodName = "sayHello";
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(HelloService.class.getName())
                .methodName(methodName)
                .argTypes(new Class[]{String.class})
                .args(new Object[]{"hello"})
                .build();

        // 构建一条完整的RPC协议消息
        RpcProtocol<RpcRequest> rpcProtocol = RpcProtocol.<RpcRequest>builder()
                .rpcHeader(rpcHeader)
                .content(rpcRequest)
                .build();

        // 创建保存RPC调用结果的RpcFuture对象
        RpcFuture<RpcResponse> rpcFuture = new RpcFuture<>(new DefaultPromise<>(new DefaultEventLoop()));
        // 保存协议唯一标识sequenceId与RpcFuture对象的映射关系
        RpcPendingHolder.putRpcFuture(sequenceId, rpcFuture);

        try {
            // 客户端invoke调用指定服务端
            NettyRpcClient.getInstance().invoke(rpcProtocol, "127.0.0.1", 1234);
        } catch (Exception e) {
            // rpc调用异常时删除对应RpcFuture
            RpcPendingHolder.removeRpcFuture(sequenceId);
            throw e;
        }

        // 获取rpc调用结果
        Promise<RpcResponse> promise = rpcFuture.getPromise();
        // todo get(0) => get() ?
        RpcResponse rpcResponse = promise.get();
        String errorMsg = rpcResponse.getErrorMsg();
        if (StringUtils.isNotBlank(errorMsg)) {
            LOGGER.error("rpc invoke failed, errorMsg: {}", errorMsg);
            throw new RpcException(errorMsg);
        }
        Object result = rpcResponse.getResult();
        LOGGER.info("rpc invoke result: {}", result);
    }
}