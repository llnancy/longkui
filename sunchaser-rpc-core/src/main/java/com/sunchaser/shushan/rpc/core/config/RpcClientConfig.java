package com.sunchaser.shushan.rpc.core.config;

import com.sunchaser.shushan.rpc.core.common.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * rpc client config
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RpcClientConfig {

    private Integer connectionTimeout = Constants.DEFAULT_CONNECTION_TIMEOUT;

    private Integer ioThreads = Constants.DEFAULT_IO_THREADS;

    public static RpcClientConfig createDefaultConfig() {
        return new RpcClientConfig();
    }
}
