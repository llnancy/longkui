package com.sunchaser.shushan.rpc.core.proxy;

import com.sunchaser.shushan.rpc.core.common.RpcContext;
import com.sunchaser.shushan.rpc.core.exceptions.RpcException;
import com.sunchaser.shushan.rpc.core.handler.RpcPendingHolder;
import com.sunchaser.shushan.rpc.core.protocol.*;
import com.sunchaser.shushan.rpc.core.registry.Registry;
import com.sunchaser.shushan.rpc.core.registry.ServiceMeta;
import com.sunchaser.shushan.rpc.core.serialize.ArrayElement;
import com.sunchaser.shushan.rpc.core.transport.NettyRpcClient;
import com.sunchaser.shushan.rpc.core.transport.RpcClient;
import io.netty.channel.DefaultEventLoop;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * an abstract proxy impl
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/8/12
 */
@Getter
@Slf4j
public abstract class AbstractProxy {

    private final String serviceName;

    private final Registry registry;

    private final Integer timeout;

    private final RpcClient<RpcRequest> rpcClient;

    public AbstractProxy(String serviceName, Registry registry) {
        this(serviceName, registry, 0);
    }

    public AbstractProxy(String serviceName, Registry registry, Integer timeout) {
        this.serviceName = serviceName;
        this.registry = registry;
        this.timeout = timeout;
        this.rpcClient = new NettyRpcClient<>();
    }

    protected Object proxyInvoke(Method method, Object[] args) throws Throwable {
        long sequenceId = RpcPendingHolder.generateSequenceId();
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

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("AbstractProxy#proxyInvoke: args: {}", Arrays.toString(args));
            LOGGER.debug("AbstractProxy#proxyInvoke: argTypes: {}", Arrays.toString(method.getParameterTypes()));
        }

        RpcProtocol<RpcRequest> rpcProtocol = RpcProtocol.<RpcRequest>builder()
                .rpcHeader(rpcHeader)
                .content(rpcRequest)
                .build();

        // 服务发现
        ServiceMeta serviceMeta = registry.discovery(serviceName, methodName);

        // rpc调用结果future对象
        RpcFuture<RpcResponse> rpcFuture = new RpcFuture<>(new DefaultPromise<>(new DefaultEventLoop()));
        RpcPendingHolder.putRpcFuture(sequenceId, rpcFuture);

        try {
            // invoke
            rpcClient.invoke(rpcProtocol, serviceMeta.getAddress(), serviceMeta.getPort());
        } catch (Exception e) {
            // rpc调用异常时删除对应RpcFuture
            RpcPendingHolder.removeRpcFuture(sequenceId);
            throw e;
        }

        // 获取rpc调用结果
        Promise<RpcResponse> promise = rpcFuture.getPromise();
        // todo get(0) => get() ?
        RpcResponse rpcResponse = timeout == 0 ? promise.get() : promise.get(timeout, TimeUnit.MILLISECONDS);
        String errorMsg = rpcResponse.getErrorMsg();
        if (StringUtils.isNotBlank(errorMsg)) {
            LOGGER.error("rpc invoke failed, errorMsg: {}", errorMsg);
            throw new RpcException(errorMsg);
        }
        return rpcResponse.getResult();
    }
}
