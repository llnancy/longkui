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
