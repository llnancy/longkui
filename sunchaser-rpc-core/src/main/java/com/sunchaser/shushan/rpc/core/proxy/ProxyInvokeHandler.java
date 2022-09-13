package com.sunchaser.shushan.rpc.core.proxy;

import com.sunchaser.shushan.rpc.core.common.RpcContext;
import com.sunchaser.shushan.rpc.core.config.RpcServiceConfig;
import com.sunchaser.shushan.rpc.core.exceptions.RpcException;
import com.sunchaser.shushan.rpc.core.handler.RpcPendingHolder;
import com.sunchaser.shushan.rpc.core.protocol.*;
import com.sunchaser.shushan.rpc.core.registry.Registry;
import com.sunchaser.shushan.rpc.core.registry.ServiceMetaData;
import com.sunchaser.shushan.rpc.core.registry.impl.ZookeeperRegistry;
import com.sunchaser.shushan.rpc.core.serialize.ArrayElement;
import com.sunchaser.shushan.rpc.core.transport.client.NettyRpcClient;
import com.sunchaser.shushan.rpc.core.transport.client.RpcClient;
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

    private final RpcServiceConfig rpcServiceConfig;

    private static final Registry REGISTRY = ZookeeperRegistry.getInstance();

    private static final RpcClient RPC_CLIENT = NettyRpcClient.getInstance();

    public ProxyInvokeHandler(RpcServiceConfig rpcServiceConfig) {
        this.rpcServiceConfig = rpcServiceConfig;
    }

    protected Object doInvoke(Method method, Object[] args) throws Throwable {
        // 构建协议头
        long sequenceId = RpcPendingHolder.generateSequenceId();
        RpcHeader rpcHeader = RpcHeader.builder()
                .magic(RpcContext.MAGIC)
                .versionAndType(RpcContext.DEFAULT_VERSION_AND_REQUEST_TYPE)
                .compressAndSerialize(RpcContext.DEFAULT_COMPRESS_SERIALIZE)
                .sequenceId(sequenceId)
                .build();

        // kryo、protostuff等序列化框架会忽略数组中间索引的null元素，这里用特殊值代替null
        ArrayElement.wrapArgs(args);
        // 构建协议体
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(rpcServiceConfig.getClassName())
                .version(rpcServiceConfig.getVersion())
                .group(rpcServiceConfig.getGroup())
                .methodName(method.getName())
                .argTypes(method.getParameterTypes())
                .args(args)
                .build();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("AbstractProxy#proxyInvoke: args: {}", Arrays.toString(args));
            LOGGER.debug("AbstractProxy#proxyInvoke: argTypes: {}", Arrays.toString(method.getParameterTypes()));
        }

        // 构建一条完整的RPC协议消息
        RpcProtocol<RpcRequest> rpcProtocol = RpcProtocol.<RpcRequest>builder()
                .rpcHeader(rpcHeader)
                .rpcBody(rpcRequest)
                .build();

        // 创建保存RPC调用结果的RpcFuture对象
        RpcFuture<RpcResponse> rpcFuture = new RpcFuture<>(new DefaultPromise<>(new DefaultEventLoop()));
        // 保存协议唯一标识sequenceId与RpcFuture对象的映射关系
        RpcPendingHolder.putRpcFuture(sequenceId, rpcFuture);

        // 服务发现
        ServiceMetaData serviceMetaData = REGISTRY.discovery(rpcServiceConfig.getRpcServiceKey());

        try {
            // invoke
            RPC_CLIENT.invoke(rpcProtocol, serviceMetaData.getHost(), serviceMetaData.getPort());
        } catch (Throwable t) {
            // rpc调用异常时删除对应RpcFuture
            RpcPendingHolder.removeRpcFuture(sequenceId);
            throw t;
        }

        // 获取rpc调用结果
        Promise<RpcResponse> promise = rpcFuture.getPromise();
        long timeout = rpcServiceConfig.getTimeout();
        // todo get(0) => get() ?
        RpcResponse rpcResponse = timeout == 0 ? promise.get() : promise.get(timeout, TimeUnit.MILLISECONDS);
        String errorMsg = rpcResponse.getErrorMsg();
        if (StringUtils.isNotBlank(errorMsg)) {
            LOGGER.error("rpc invoke failed, errorMsg: {}", errorMsg);
            throw new RpcException(errorMsg);
        }
        return rpcResponse.getResult();
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
