package com.sunchaser.shushan.rpc.core.config;

import com.sunchaser.shushan.rpc.core.extension.ExtensionLoader;
import com.sunchaser.shushan.rpc.core.registry.Registry;
import com.sunchaser.shushan.rpc.core.transport.client.RpcClient;
import com.sunchaser.shushan.rpc.core.transport.server.RpcServer;
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
        destroyRegistry();
        // destroy rpc server
        destroyRpcServer();
        // destroy thread pool
        ThreadPools.shutDownAll();
        // destroy rpc client
        destroyRpcClient();
    }

    private static void destroyRegistry() {
        ExtensionLoader<Registry> loader = ExtensionLoader.getExtensionLoader(Registry.class);
        for (String registryName : loader.getLoadedExtensions()) {
            try {
                Registry registry = loader.getLoadedExtension(registryName);
                if (Objects.nonNull(registry)) {
                    LOGGER.info("shutdown {} Registry now.", registryName);
                    registry.destroy();
                }
            } catch (Throwable t) {
                LOGGER.warn(t.getMessage(), t);
            }
        }
    }

    private static void destroyRpcServer() {
        ExtensionLoader<RpcServer> loader = ExtensionLoader.getExtensionLoader(RpcServer.class);
        for (String rpcServerName : loader.getLoadedExtensions()) {
            try {
                RpcServer rpcServer = loader.getLoadedExtension(rpcServerName);
                if (Objects.nonNull(rpcServer)) {
                    LOGGER.info("shutdown {} RpcServer now.", rpcServerName);
                    rpcServer.destroy();
                }
            } catch (Throwable t) {
                LOGGER.warn(t.getMessage(), t);
            }
        }
    }

    private static void destroyRpcClient() {
        ExtensionLoader<RpcClient> loader = ExtensionLoader.getExtensionLoader(RpcClient.class);
        for (String rpcClientName : loader.getLoadedExtensions()) {
            try {
                RpcClient rpcClient = loader.getLoadedExtension(rpcClientName);
                if (Objects.nonNull(rpcClient)) {
                    LOGGER.info("shutdown {} RpcClient now.", rpcClientName);
                    rpcClient.destroy();
                }
            } catch (Throwable t) {
                LOGGER.warn(t.getMessage(), t);
            }
        }
    }
}
