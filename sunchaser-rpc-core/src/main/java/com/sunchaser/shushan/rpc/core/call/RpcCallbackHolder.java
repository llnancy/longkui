package com.sunchaser.shushan.rpc.core.call;

/**
 * thread hold the rpc invoke callback
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/20
 */
public class RpcCallbackHolder {

    private static final ThreadLocal<RpcCallback<?>> RPC_INVOKE_CALLBACK_THREAD_LOCAL = new ThreadLocal<>();

    public static RpcCallback<?> getCallback() {
        RpcCallback<?> rpcCallback = RPC_INVOKE_CALLBACK_THREAD_LOCAL.get();
        removeCallback();
        return rpcCallback;
    }

    public static void setCallback(RpcCallback<?> rpcCallback) {
        RPC_INVOKE_CALLBACK_THREAD_LOCAL.set(rpcCallback);
    }

    public static void removeCallback() {
        RPC_INVOKE_CALLBACK_THREAD_LOCAL.remove();
    }
}
