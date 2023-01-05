/*
 * Copyright 2022 LongKui
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

package io.github.llnancy.longkui.core.proxy;

import cn.hutool.core.lang.func.VoidFunc0;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import io.github.llnancy.longkui.core.call.CallType;
import io.github.llnancy.longkui.core.call.RpcCallback;
import io.github.llnancy.longkui.core.call.RpcCallbackHolder;
import io.github.llnancy.longkui.core.call.RpcFutureHolder;
import io.github.llnancy.longkui.core.call.RpcInvokeFuture;
import io.github.llnancy.longkui.core.common.Constants;
import io.github.llnancy.longkui.core.common.RpcContext;
import io.github.llnancy.longkui.core.common.RpcMessageTypeEnum;
import io.github.llnancy.longkui.core.compress.Compressor;
import io.github.llnancy.longkui.core.config.RpcClientConfig;
import io.github.llnancy.longkui.core.config.RpcProtocolConfig;
import io.github.llnancy.longkui.core.config.RpcServiceConfig;
import io.github.llnancy.longkui.core.exceptions.RpcException;
import io.github.llnancy.longkui.core.extension.ExtensionLoader;
import io.github.llnancy.longkui.core.handler.RpcPendingHolder;
import io.github.llnancy.longkui.core.protocol.RpcFuture;
import io.github.llnancy.longkui.core.protocol.RpcHeader;
import io.github.llnancy.longkui.core.protocol.RpcProtocol;
import io.github.llnancy.longkui.core.protocol.RpcRequest;
import io.github.llnancy.longkui.core.protocol.RpcResponse;
import io.github.llnancy.longkui.core.registry.Registry;
import io.github.llnancy.longkui.core.registry.ServiceMetaData;
import io.github.llnancy.longkui.core.serialize.ArrayElement;
import io.github.llnancy.longkui.core.serialize.Serializer;
import io.github.llnancy.longkui.core.transport.client.NettyRpcClient;
import io.github.llnancy.longkui.core.transport.client.RpcClient;
import io.github.llnancy.longkui.core.uid.SequenceIdGenerator;
import io.netty.channel.DefaultEventLoop;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;
import javassist.util.proxy.MethodHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
import java.util.Map;
import java.util.Optional;
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
public class DynamicProxyHandler implements InvocationHandler, MethodInterceptor, MethodHandler {

    /**
     * rpc protocol config
     */
    private final RpcProtocolConfig rpcProtocolConfig;

    /**
     * rpc service config
     */
    private final RpcServiceConfig rpcServiceConfig;

    /**
     * service registry
     */
    private final Registry registry;

    /**
     * rpc client
     */
    private final RpcClient rpcClient;

    /**
     * sequence id generator
     */
    private final SequenceIdGenerator sequenceIdGenerator;

    /**
     * serializer
     */
    private final Serializer serializer;

    /**
     * compressor
     */
    private final Compressor compressor;

    /**
     * constructor
     *
     * @param rpcClientConfig  rpc client config
     * @param rpcServiceConfig rpc service config
     */
    public DynamicProxyHandler(RpcClientConfig rpcClientConfig, RpcServiceConfig rpcServiceConfig) {
        this.rpcProtocolConfig = rpcClientConfig.getRpcProtocolConfig();
        this.rpcServiceConfig = rpcServiceConfig;
        this.registry = ExtensionLoader.getExtensionLoader(Registry.class).getExtension(rpcClientConfig.getRegistry());
        String rpcClientType = rpcClientConfig.getRpcClient();
        if (Constants.NETTY.equals(rpcClientType)) {
            this.rpcClient = new NettyRpcClient(rpcClientConfig);
        } else {
            this.rpcClient = ExtensionLoader.getExtensionLoader(RpcClient.class).getExtension(rpcClientType);
        }
        this.sequenceIdGenerator = ExtensionLoader.getExtensionLoader(SequenceIdGenerator.class).getExtension(this.rpcProtocolConfig.getSequenceIdGenerator());
        this.serializer = ExtensionLoader.getExtensionLoader(Serializer.class).getExtension(this.rpcProtocolConfig.getSerializer());
        this.compressor = ExtensionLoader.getExtensionLoader(Compressor.class).getExtension(this.rpcProtocolConfig.getCompressor());
    }

    /**
     * do invoke.
     * adapt to different dynamic proxy.
     *
     * @param method java.lang.reflect.Method
     * @param args   Object Array
     * @return rpc result
     * @throws Throwable rpc error
     */
    protected Object doInvoke(Method method, Object[] args) throws Throwable {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("AbstractProxy#proxyInvoke: args: {}", Arrays.toString(args));
            LOGGER.debug("AbstractProxy#proxyInvoke: argTypes: {}", Arrays.toString(method.getParameterTypes()));
        }
        long sequenceId = this.sequenceIdGenerator.nextSequenceId();
        // 构建一条完整的RPC协议消息
        RpcProtocol<RpcRequest> rpcProtocol = buildRpcProtocol(method, args, sequenceId);

        // 服务发现
        ServiceMetaData serviceMetaData = registry.discovery(rpcServiceConfig.getRpcServiceKey());

        // build rpc caller context
        RpcCallerContext rpcCallerContext = RpcCallerContext.builder()
                .caller(() -> rpcClient.invoke(rpcProtocol, serviceMetaData.getHost(), serviceMetaData.getPort()))
                .timeout(rpcServiceConfig.getTimeout())
                .sequenceId(sequenceId)
                .build();

        // do call
        return RpcCaller.match(rpcServiceConfig.getCallType())
                .call(rpcCallerContext);
    }

    /**
     * rpc caller.
     * Contains four calling methods.
     * Use the strategy pattern and template method design pattern and simple factory pattern
     */
    @AllArgsConstructor
    @Getter
    enum RpcCaller {

        /**
         * sync caller
         */
        SYNC_CALLER(CallType.SYNC) {
            @Override
            protected Object afterCall() throws Throwable {
                RpcCallerContext rpcCallerContext = rpcCallerContextThreadLocal.get();
                long sequenceId = rpcCallerContext.getSequenceId();
                RpcFuture<RpcResponse> rpcFuture = RpcPendingHolder.getRpcFuture(sequenceId);
                Promise<RpcResponse> promise = rpcFuture.getPromise();
                // todo get(0) => get() ?
                long timeout = rpcCallerContext.getTimeout();
                RpcResponse rpcResponse = timeout == 0 ? promise.get() : promise.get(timeout, TimeUnit.MILLISECONDS);
                String errorMsg = rpcResponse.getErrorMsg();
                if (StringUtils.isNotBlank(errorMsg)) {
                    LOGGER.error("LongKui >>>>>> rpc invoke failed, errorMsg: {}.", errorMsg);
                    throw new RpcException(errorMsg);
                }
                return rpcResponse.getResult();
            }
        },

        /**
         * future caller
         */
        FUTURE_CALLER(CallType.FUTURE) {
            @Override
            protected void beforeCall() {
                super.beforeCall();
                RpcCallerContext rpcCallerContext = rpcCallerContextThreadLocal.get();
                long sequenceId = rpcCallerContext.getSequenceId();
                RpcFuture<RpcResponse> rpcFuture = RpcPendingHolder.getRpcFuture(sequenceId);
                RpcFutureHolder.setFuture(new RpcInvokeFuture<>(rpcFuture.getPromise()));
            }
        },

        /**
         * callback caller
         */
        CALLBACK_CALLER(CallType.CALLBACK) {
            @Override
            protected void beforeCall() {
                RpcCallback<?> rpcCallback = RpcCallbackHolder.getCallback();
                Preconditions.checkNotNull(rpcCallback, "LongKui >>>>>> RpcInvokeCallback(CallType = CALLBACK) instance cannot be null.");
                RpcCallerContext rpcCallerContext = rpcCallerContextThreadLocal.get();
                long sequenceId = rpcCallerContext.getSequenceId();
                // 创建包含rpcCallback的保存RPC调用结果的RpcFuture对象
                RpcFuture<RpcResponse> rpcFuture = new RpcFuture<>(new DefaultPromise<>(new DefaultEventLoop()), rpcCallback);
                // 保存协议唯一标识sequenceId与RpcFuture对象的映射关系
                RpcPendingHolder.putRpcFuture(sequenceId, rpcFuture);
            }
        },

        /**
         * oneway caller
         */
        ONEWAY_CALLER(CallType.ONEWAY) {
            @Override
            protected void beforeCall() {
            }

            @Override
            protected void onException() {
            }
        };

        /**
         * rpc call type
         */
        private final CallType callType;

        private static final Map<CallType, RpcCaller> ENUM_MAP = Maps.newHashMap();

        static {
            for (RpcCaller rpcCaller : RpcCaller.values()) {
                ENUM_MAP.put(rpcCaller.callType, rpcCaller);
            }
        }

        public static RpcCaller match(CallType callType) {
            return Optional.ofNullable(ENUM_MAP.get(callType))
                    .orElseThrow(() -> new RpcException("LongKui >>>>>> CallType of " + callType + " is not supported!"));
        }

        /**
         * use ThreadLocal to pass RpcCallerContext.
         */
        protected final ThreadLocal<RpcCallerContext> rpcCallerContextThreadLocal = new ThreadLocal<>();

        /**
         * rpc call
         *
         * @param rpcCallerContext rpc caller context
         * @return rpc call result
         * @throws Throwable throws eg.Future#get
         */
        Object call(RpcCallerContext rpcCallerContext) throws Throwable {
            try {
                rpcCallerContextThreadLocal.set(rpcCallerContext);
                beforeCall();
                // do call
                rpcCallerContext.getCaller().call();
                return afterCall();
            } catch (Throwable t) {
                onException();
                throw t;
            } finally {
                rpcCallerContextThreadLocal.remove();
            }
        }

        /**
         * template method. before rpc call.
         */
        protected void beforeCall() {
            RpcCallerContext rpcCallerContext = rpcCallerContextThreadLocal.get();
            long sequenceId = rpcCallerContext.getSequenceId();
            // 创建保存RPC调用结果的RpcFuture对象
            RpcFuture<RpcResponse> rpcFuture = new RpcFuture<>(new DefaultPromise<>(new DefaultEventLoop()));
            // 保存协议唯一标识sequenceId与RpcFuture对象的映射关系
            RpcPendingHolder.putRpcFuture(sequenceId, rpcFuture);
        }

        protected Object afterCall() throws Throwable {
            return null;
        }

        protected void onException() {
            RpcCallerContext rpcCallerContext = rpcCallerContextThreadLocal.get();
            long sequenceId = rpcCallerContext.getSequenceId();
            // rpc调用异常时删除对应RpcFuture
            RpcPendingHolder.removeRpcFuture(sequenceId);
        }
    }

    /**
     * rpc caller context
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    static class RpcCallerContext {

        /**
         * caller. Functional programming, passing methods through parameters
         */
        private VoidFunc0 caller;

        /**
         * rpc future.get() timeout
         */
        private long timeout;

        /**
         * rpc sequence id
         */
        private long sequenceId;
    }

    /**
     * build rpc protocol
     *
     * @param method     Method
     * @param args       Object array
     * @param sequenceId sequence
     * @return RpcProtocol
     */
    private RpcProtocol<RpcRequest> buildRpcProtocol(Method method, Object[] args, long sequenceId) {
        // 构建协议头
        RpcHeader rpcHeader = buildRpcHeader(sequenceId);
        // kryo、protostuff等序列化框架会忽略数组中间索引的null元素，这里用特殊值代替null
        ArrayElement.wrapArgs(args);
        // 构建协议体
        RpcRequest rpcRequest = buildRpcRequest(method, args);
        return RpcProtocol.<RpcRequest>builder()
                .rpcHeader(rpcHeader)
                .rpcBody(rpcRequest)
                .build();
    }

    /**
     * build rpc header
     *
     * @param sequenceId sequence
     * @return RpcHeader
     */
    private RpcHeader buildRpcHeader(long sequenceId) {
        return RpcHeader.builder()
                .magic(RpcContext.MAGIC)
                .version(rpcProtocolConfig.getProtocolVersion())
                .type(RpcMessageTypeEnum.REQUEST.getCode())
                .serialize(serializer.getTypeId())
                .compress(compressor.getTypeId())
                .sequenceId(sequenceId)
                .build();
    }

    /**
     * build rpc request
     *
     * @param method Method
     * @param args   Object array
     * @return RpcRequest
     */
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

    /**
     * JDK动态代理
     *
     * @return proxy object
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return doInvoke(method, args);
    }

    /**
     * cglib动态代理
     *
     * @return proxy object
     */
    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        return doInvoke(method, args);
    }

    /**
     * javassist动态代理
     *
     * @return proxy object
     */
    @Override
    public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
        return doInvoke(thisMethod, args);
    }

    /**
     * byte buddy动态代理
     *
     * @param proxy  proxy
     * @param method Method
     * @param args   Object array
     * @return proxy object
     * @throws Throwable throws
     */
    @RuntimeType
    public Object byteBuddyInvoke(@This Object proxy, @Origin Method method, @AllArguments @RuntimeType Object[] args) throws Throwable {
        return doInvoke(method, args);
    }
}
