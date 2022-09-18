package com.sunchaser.shushan.rpc.core.config;

import com.sunchaser.shushan.rpc.core.extension.ExtensionLoader;
import com.sunchaser.shushan.rpc.core.registry.Registry;
import com.sunchaser.shushan.rpc.core.util.ThreadPools;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * rpc shutdown hook. refer to com.alibaba.dubbo.config.DubboShutdownHook
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/18
 */
@Slf4j
public class RpcShutdownHook extends Thread {

    private static final RpcShutdownHook RPC_SHUTDOWN_HOOK = new RpcShutdownHook();

    public static RpcShutdownHook getRpcShutdownHook() {
        return RPC_SHUTDOWN_HOOK;
    }

    /**
     * Has it already been destroyed or not?
     */
    private final AtomicBoolean destroyed;

    public RpcShutdownHook() {
        super("RpcShutdownHook");
        this.destroyed = new AtomicBoolean(false);
    }

    @Override
    public void run() {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Run rpc shutdown hook now.");
        }
        destroyAll();
    }

    public void destroyAll() {
        if (!destroyed.compareAndSet(false, true)) {
            return;
        }
        // destroy all the registry
        ExtensionLoader<Registry> loader = ExtensionLoader.getExtensionLoader(Registry.class);
        for (String registryName : loader.getLoadedExtensions()) {
            try {
                Registry registry = loader.getLoadedExtension(registryName);
                if (Objects.nonNull(registry)) {
                    registry.destroy();
                }
            } catch (Throwable t) {
                LOGGER.warn(t.getMessage(), t);
            }
        }
        // destroy thread pool
        ThreadPools.shutDownAll();
    }
}
