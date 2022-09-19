package com.sunchaser.shushan.rpc.core.call;

/**
 * rpc invoke callback
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/20
 */
public interface RpcCallback<T> {

    /**
     * on success callback
     *
     * @param result invoke result
     */
    void onSuccess(T result);

    /**
     * on error callback
     *
     * @param t error
     */
    void onError(Throwable t);
}
