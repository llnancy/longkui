package com.sunchaser.shushan.rpc.core.protocol;

import com.sunchaser.shushan.rpc.core.call.RpcCallback;
import io.netty.util.concurrent.Promise;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * rpc future
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RpcFuture<T> {

    private Promise<T> promise;

    private RpcCallback<?> rpcCallback;

    public RpcFuture(Promise<T> promise) {
        this.promise = promise;
        this.rpcCallback = null;
    }
}
