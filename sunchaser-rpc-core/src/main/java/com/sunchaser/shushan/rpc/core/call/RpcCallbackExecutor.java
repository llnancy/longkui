package com.sunchaser.shushan.rpc.core.call;

import com.sunchaser.shushan.rpc.core.config.ThreadPoolConfig;
import com.sunchaser.shushan.rpc.core.util.ThreadPools;

import java.util.concurrent.ExecutorService;

/**
 * execute the rpc callback
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/20
 */
public class RpcCallbackExecutor {

    private final ExecutorService callbackExecutor;

    public RpcCallbackExecutor(ThreadPoolConfig config) {
        config.setThreadNameIdentifier(this.getClass().getName());
        this.callbackExecutor = ThreadPools.createThreadPoolIfAbsent(config);
    }

    public void execute(Runnable callback) {
        callbackExecutor.execute(callback);
    }

    public void shutdown() {
        ThreadPools.shutdown(callbackExecutor);
    }
}
