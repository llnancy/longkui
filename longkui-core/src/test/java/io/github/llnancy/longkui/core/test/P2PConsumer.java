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

package io.github.llnancy.longkui.core.test;

import io.github.llnancy.longkui.core.common.Constants;
import io.github.llnancy.longkui.core.common.RpcContext;
import io.github.llnancy.longkui.core.common.RpcMessageTypeEnum;
import io.github.llnancy.longkui.core.config.RpcClientConfig;
import io.github.llnancy.longkui.core.config.RpcServiceConfig;
import io.github.llnancy.longkui.core.exceptions.RpcException;
import io.github.llnancy.longkui.core.handler.RpcPendingHolder;
import io.github.llnancy.longkui.core.proxy.DynamicProxy;
import io.github.llnancy.longkui.core.proxy.impl.JavassistDynamicProxy;
import io.github.llnancy.longkui.core.transport.client.NettyRpcClient;
import io.github.llnancy.longkui.core.uid.impl.AtomicLongIdGenerator;
import io.github.llnancy.longkui.core.protocol.RpcFuture;
import io.github.llnancy.longkui.core.protocol.RpcHeader;
import io.github.llnancy.longkui.core.protocol.RpcProtocol;
import io.github.llnancy.longkui.core.protocol.RpcRequest;
import io.github.llnancy.longkui.core.protocol.RpcResponse;
import io.netty.channel.DefaultEventLoop;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.ExecutionException;

/**
 * 点对点RPC服务消费者
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/8/18
 */
@Slf4j
public class P2PConsumer {

    public static void main(String[] args) throws Exception {
        // p2pConsumer();
        RpcClientConfig rpcClientConfig = RpcClientConfig.createDefaultConfig();
        RpcServiceConfig rpcServiceConfig = RpcServiceConfig.createDefaultConfig(HelloService.class);
        DynamicProxy dynamicProxy = JavassistDynamicProxy.getInstance();
        HelloService helloService = dynamicProxy.createProxyInstance(rpcClientConfig, rpcServiceConfig);
        String hello = helloService.sayHello("SunChaser");
        LOGGER.info("sayHello result: {}", hello);
    }

    private static void p2pConsumer() throws InterruptedException, ExecutionException {
        // 构建协议头
        long sequenceId = new AtomicLongIdGenerator().nextSequenceId();
        RpcHeader rpcHeader = RpcHeader.builder()
                .magic(RpcContext.MAGIC)
                .version(Constants.DEFAULT_PROTOCOL_VERSION)
                .type(RpcMessageTypeEnum.REQUEST.getCode())
                .serialize(Constants.DEFAULT_SERIALIZE)
                .compress(Constants.DEFAULT_COMPRESS)
                .sequenceId(sequenceId)
                .build();

        // 构建协议体：调用HelloService的sayHello方法，传递参数hello
        String methodName = "sayHello";
        String version = "0.0.1";
        String group = "default";
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(HelloService.class.getName())
                .version(version)
                .group(group)
                .methodName(methodName)
                .argTypes(new Class[]{String.class})
                .args(new Object[]{"hello"})
                .build();

        // 构建一条完整的RPC协议消息
        RpcProtocol<RpcRequest> rpcProtocol = RpcProtocol.<RpcRequest>builder()
                .rpcHeader(rpcHeader)
                .rpcBody(rpcRequest)
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
