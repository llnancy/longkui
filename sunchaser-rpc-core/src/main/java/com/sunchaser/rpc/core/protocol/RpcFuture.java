package com.sunchaser.rpc.core.protocol;

import io.netty.util.concurrent.Promise;
import lombok.Data;

/**
 * rpc future
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/14
 */
@Data
public class RpcFuture<T> {

    private Promise<T> promise;
}
