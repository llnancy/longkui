package com.sunchaser.shushan.rpc.core.common;

import com.sunchaser.shushan.rpc.core.registry.RegistryEnum;

/**
 * 常量
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/8/16
 */
public interface Constants {

    String DEFAULT = "default";

    String NETTY = "netty";

    String UNDERLINE = "_";

    String EMPTY = "";

    /**
     * version: 1
     */
    byte DEFAULT_PROTOCOL_VERSION = (byte) 1;

    /**
     * Hessian2
     */
    byte DEFAULT_SERIALIZE = (byte) 0;

    /**
     * Snappy
     */
    byte DEFAULT_COMPRESS = (byte) 1;

    int DEFAULT_CONNECTION_TIMEOUT = 3000;

    int DEFAULT_IO_THREADS = Math.min(Runtime.getRuntime().availableProcessors() + 1, 32);

    /**
     * default registry implementation. zookeeper
     */
    String DEFAULT_REGISTRY = RegistryEnum.ZOOKEEPER.name().toLowerCase();
}
