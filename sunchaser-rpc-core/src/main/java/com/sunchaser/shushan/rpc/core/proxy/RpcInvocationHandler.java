package com.sunchaser.shushan.rpc.core.proxy;

import com.sunchaser.shushan.rpc.core.common.RpcContext;
import com.sunchaser.shushan.rpc.core.exceptions.RpcException;
import com.sunchaser.shushan.rpc.core.handler.RpcResponseHolder;
import com.sunchaser.shushan.rpc.core.registry.Registry;
import com.sunchaser.shushan.rpc.core.registry.ServiceMeta;
import com.sunchaser.shushan.rpc.core.serialize.ArrayElement;
import com.sunchaser.shushan.rpc.core.transport.NettyRpcClient;
import com.sunchaser.shushan.rpc.core.protocol.*;
import io.netty.channel.DefaultEventLoop;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * 基于JDK的动态代理实现
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/15
 */
@Slf4j
public class RpcInvocationHandler implements InvocationHandler {

    private final String serviceName;

    private final Registry registry;

    private final int timeout;

    public RpcInvocationHandler(String serviceName, Registry registry) {
        this(serviceName, registry, 0);
    }

    public RpcInvocationHandler(String serviceName, Registry registry, int timeout) {
        this.serviceName = serviceName;
        this.registry = registry;
        this.timeout = timeout;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        long sequenceId = RpcResponseHolder.generateSequenceId();
        RpcHeader rpcHeader = RpcHeader.builder()
                .magic(RpcContext.MAGIC)
                .versionAndType(RpcContext.DEFAULT_VERSION_AND_TYPE)
                .compressAndSerialize(RpcContext.DEFAULT_COMPRESS_SERIALIZE)
                .sequenceId(sequenceId)
                .build();
        String methodName = method.getName();
        // kryo、protostuff等序列化框架会忽略数组中间索引的null元素，这里用特殊值代替null
        ArrayElement.wrapArgs(args);

        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(serviceName)
                .methodName(methodName)
                .argTypes(method.getParameterTypes())
                .args(args)
                .build();

        log.info("RpcInvocationHandler.invoke: args: {}", Arrays.toString(args));
        log.info("RpcInvocationHandler.invoke: argTypes: {}", Arrays.toString(method.getParameterTypes()));

        RpcProtocol<RpcRequest> rpcProtocol = RpcProtocol.<RpcRequest>builder()
                .rpcHeader(rpcHeader)
                .content(rpcRequest)
                .build();
        // 服务发现
        ServiceMeta serviceMeta = registry.discovery(serviceName, methodName);
        // rpc调用结果future对象
        RpcFuture<RpcResponse> rpcFuture = new RpcFuture<>(new DefaultPromise<>(new DefaultEventLoop()));
        RpcResponseHolder.putRpcFuture(sequenceId, rpcFuture);
        NettyRpcClient<RpcRequest> nettyRpcClient = new NettyRpcClient<>(serviceMeta.getAddress(), serviceMeta.getPort());
        // todo 连接复用
        nettyRpcClient.connect();
        // invoke
        nettyRpcClient.invoke(rpcProtocol);
        // 获取rpc结果
        Promise<RpcResponse> promise = rpcFuture.getPromise();
        // todo get(0) => get() ?
        RpcResponse rpcResponse = timeout == 0 ? promise.get() : promise.get(timeout, TimeUnit.MILLISECONDS);
        String errorMsg = rpcResponse.getErrorMsg();
        if (StringUtils.isNotBlank(errorMsg)) {
            log.error("rpc invoke failed, errorMsg: {}", errorMsg);
            throw new RpcException(errorMsg);
        }
        return rpcResponse.getResult();
    }
}
