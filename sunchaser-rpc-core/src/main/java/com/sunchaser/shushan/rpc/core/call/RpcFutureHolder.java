package com.sunchaser.shushan.rpc.core.call;

import com.sunchaser.shushan.rpc.core.protocol.RpcFuture;
import com.sunchaser.shushan.rpc.core.protocol.RpcResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * thread hold the rpc response future
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/19
 */
@Slf4j
public class RpcFutureHolder {

    private static final ThreadLocal<RpcFuture<RpcResponse>> RPC_FUTURE_THREAD_LOCAL = new ThreadLocal<>();

    public static RpcFuture<RpcResponse> getFuture() {
        RpcFuture<RpcResponse> future = RPC_FUTURE_THREAD_LOCAL.get();
        removeFuture();
        return future;
    }

    public static void setFuture(RpcFuture<RpcResponse> future) {
        RPC_FUTURE_THREAD_LOCAL.set(future);
    }

    public static void removeFuture() {
        RPC_FUTURE_THREAD_LOCAL.remove();
    }
}
