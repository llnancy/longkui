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

package com.sunchaser.shushan.rpc.core.call;

import com.sunchaser.shushan.rpc.core.exceptions.RpcException;
import com.sunchaser.shushan.rpc.core.protocol.RpcResponse;
import io.netty.util.concurrent.Promise;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * rpc invoke future
 * delegate the rpc future
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/22
 */
@Slf4j
public class RpcInvokeFuture<T> implements Future<T> {

    private final Promise<RpcResponse> promise;

    public RpcInvokeFuture(Promise<RpcResponse> promise) {
        this.promise = promise;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return promise.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return promise.isCancelled();
    }

    @Override
    public boolean isDone() {
        return promise.isDone();
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        return doGet(promise.get());
    }

    @Override
    public T get(long timeout, @NonNull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return doGet(promise.get(timeout, unit));
    }

    /**
     * do get rpc response
     *
     * @param rpcResponse rpc response
     * @return result
     */
    @SuppressWarnings("unchecked")
    private T doGet(RpcResponse rpcResponse) {
        String errorMsg = rpcResponse.getErrorMsg();
        if (StringUtils.isNotBlank(errorMsg)) {
            LOGGER.error("sunchaser-rpc >>>>>> rpc invoke failed, errorMsg: {}.", errorMsg);
            throw new RpcException(errorMsg);
        }
        return (T) rpcResponse.getResult();
    }
}
