package com.sunchaser.shushan.rpc.core.call;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Future;

/**
 * thread hold the rpc response future
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/19
 */
@Slf4j
public class RpcFutureHolder {

    private static final ThreadLocal<RpcInvokeFuture<?>> RPC_FUTURE_THREAD_LOCAL = new ThreadLocal<>();

    public static <T> Future<T> getFuture() {
        @SuppressWarnings("unchecked")
        Future<T> future = (Future<T>) RPC_FUTURE_THREAD_LOCAL.get();
        removeFuture();
        return future;
    }

    public static void setFuture(RpcInvokeFuture<?> invokeFuture) {
        RPC_FUTURE_THREAD_LOCAL.set(invokeFuture);
    }

    public static void removeFuture() {
        RPC_FUTURE_THREAD_LOCAL.remove();
    }
}
