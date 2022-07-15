package com.sunchaser.rpc.core.protocol;

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
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RpcFuture<T> {

    private Promise<T> promise;
}
