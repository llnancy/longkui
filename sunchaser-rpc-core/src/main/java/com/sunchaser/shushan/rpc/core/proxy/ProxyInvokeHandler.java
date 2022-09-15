package com.sunchaser.shushan.rpc.core.proxy;

import com.sunchaser.shushan.rpc.core.common.RpcContext;
import com.sunchaser.shushan.rpc.core.common.RpcMessageTypeEnum;
import com.sunchaser.shushan.rpc.core.compress.Compressor;
import com.sunchaser.shushan.rpc.core.config.RpcExtensionConfig;
import com.sunchaser.shushan.rpc.core.config.RpcFrameworkConfig;
import com.sunchaser.shushan.rpc.core.config.RpcProtocolConfig;
import com.sunchaser.shushan.rpc.core.config.RpcServiceConfig;
import com.sunchaser.shushan.rpc.core.exceptions.RpcException;
import com.sunchaser.shushan.rpc.core.extension.ExtensionLoader;
import com.sunchaser.shushan.rpc.core.handler.RpcPendingHolder;
import com.sunchaser.shushan.rpc.core.protocol.*;
import com.sunchaser.shushan.rpc.core.registry.Registry;
import com.sunchaser.shushan.rpc.core.registry.ServiceMetaData;
import com.sunchaser.shushan.rpc.core.serialize.ArrayElement;
import com.sunchaser.shushan.rpc.core.serialize.Serializer;
import com.sunchaser.shushan.rpc.core.transport.client.RpcClient;
import com.sunchaser.shushan.rpc.core.uid.SequenceIdGenerator;
import io.netty.channel.DefaultEventLoop;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;
import javassist.util.proxy.MethodHandler;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * method invoke handler
 * <p>
 * JDK动态代理：java.lang.reflect.InvocationHandler
 * cglib动态代理：net.sf.cglib.proxy.MethodInterceptor
 * javassist动态代理：javassist.util.proxy.MethodHandler
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/8/12
 */
@Slf4j
public class ProxyInvokeHandler implements InvocationHandler, MethodInterceptor, MethodHandler {

    private final RpcProtocolConfig rpcProtocolConfig;

    private final RpcServiceConfig rpcServiceConfig;

    private final Registry registry;

    private final RpcClient rpcClient;

    private final SequenceIdGenerator sequenceIdGenerator;

    private final Serializer serializer;

    private final Compressor compressor;

    public ProxyInvokeHandler(RpcFrameworkConfig rpcFrameworkConfig) {
        this.rpcProtocolConfig = rpcFrameworkConfig.getRpcProtocolConfig();
        this.rpcServiceConfig = rpcFrameworkConfig.getRpcServiceConfig();
        RpcExtensionConfig rpcExtensionConfig = rpcFrameworkConfig.getRpcExtensionConfig();
        this.registry = ExtensionLoader.getExtensionLoader(Registry.class).getExtension(rpcExtensionConfig.getRegistry());
        this.rpcClient = ExtensionLoader.getExtensionLoader(RpcClient.class).getExtension(rpcExtensionConfig.getRpcClient());
        this.sequenceIdGenerator = ExtensionLoader.getExtensionLoader(SequenceIdGenerator.class).getExtension(this.rpcProtocolConfig.getSequenceIdGenerator());
        this.serializer = ExtensionLoader.getExtensionLoader(Serializer.class).getExtension(this.rpcProtocolConfig.getSerializer());
        this.compressor = ExtensionLoader.getExtensionLoader(Compressor.class).getExtension(this.rpcProtocolConfig.getCompressor());
    }

    protected Object doInvoke(Method method, Object[] args) throws Throwable {
        long sequenceId = this.sequenceIdGenerator.nextSequenceId();
        // 构建协议头
        RpcHeader rpcHeader = buildRpcHeader(sequenceId);

        // kryo、protostuff等序列化框架会忽略数组中间索引的null元素，这里用特殊值代替null
        ArrayElement.wrapArgs(args);

        // 构建协议体
        RpcRequest rpcRequest = buildRpcRequest(method, args);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("AbstractProxy#proxyInvoke: args: {}", Arrays.toString(args));
            LOGGER.debug("AbstractProxy#proxyInvoke: argTypes: {}", Arrays.toString(method.getParameterTypes()));
        }

        // 构建一条完整的RPC协议消息
        RpcProtocol<RpcRequest> rpcProtocol = buildRpcProtocol(rpcHeader, rpcRequest);

        // 创建保存RPC调用结果的RpcFuture对象
        RpcFuture<RpcResponse> rpcFuture = new RpcFuture<>(new DefaultPromise<>(new DefaultEventLoop()));
        // 保存协议唯一标识sequenceId与RpcFuture对象的映射关系
        RpcPendingHolder.putRpcFuture(sequenceId, rpcFuture);

        // 服务发现
        ServiceMetaData serviceMetaData = registry.discovery(rpcServiceConfig.getRpcServiceKey());

        try {
            // invoke
            rpcClient.invoke(rpcProtocol, serviceMetaData.getHost(), serviceMetaData.getPort());
        } catch (Throwable t) {
            // rpc调用异常时删除对应RpcFuture
            RpcPendingHolder.removeRpcFuture(sequenceId);
            throw t;
        }

        // 获取rpc调用结果
        RpcResponse rpcResponse = getRpcResponse(rpcFuture);
        return rpcResponse.getResult();
    }

    private RpcResponse getRpcResponse(RpcFuture<RpcResponse> rpcFuture) throws Throwable {
        Promise<RpcResponse> promise = rpcFuture.getPromise();
        long timeout = rpcServiceConfig.getTimeout();
        // todo get(0) => get() ?
        RpcResponse rpcResponse = timeout == 0 ? promise.get() : promise.get(timeout, TimeUnit.MILLISECONDS);
        String errorMsg = rpcResponse.getErrorMsg();
        if (StringUtils.isNotBlank(errorMsg)) {
            LOGGER.error("rpc invoke failed, errorMsg: {}", errorMsg);
            throw new RpcException(errorMsg);
        }
        return rpcResponse;
    }

    private static RpcProtocol<RpcRequest> buildRpcProtocol(RpcHeader rpcHeader, RpcRequest rpcRequest) {
        return RpcProtocol.<RpcRequest>builder()
                .rpcHeader(rpcHeader)
                .rpcBody(rpcRequest)
                .build();
    }

    private RpcRequest buildRpcRequest(Method method, Object[] args) {
        return RpcRequest.builder()
                .serviceName(rpcServiceConfig.getClassName())
                .version(rpcServiceConfig.getVersion())
                .group(rpcServiceConfig.getGroup())
                .methodName(method.getName())
                .argTypes(method.getParameterTypes())
                .args(args)
                .build();
    }

    private RpcHeader buildRpcHeader(long sequenceId) {
        return RpcHeader.builder()
                .magic(RpcContext.MAGIC)
                .version(rpcProtocolConfig.getProtocolVersion())
                .type(RpcMessageTypeEnum.REQUEST.getCode())
                .serialize(serializer.getContentTypeId())
                .compress(compressor.getContentTypeId())
                .sequenceId(sequenceId)
                .build();
    }

    /**
     * JDK动态代理
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return doInvoke(method, args);
    }

    /**
     * cglib动态代理
     */
    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        return doInvoke(method, args);
    }

    /**
     * javassist动态代理
     */
    @Override
    public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
        return doInvoke(thisMethod, args);
    }

    /**
     * byte buddy动态代理
     */
    @RuntimeType
    public Object byteBuddyInvoke(@This Object proxy, @Origin Method method, @AllArguments @RuntimeType Object[] args) throws Throwable {
        return doInvoke(method, args);
    }
}
