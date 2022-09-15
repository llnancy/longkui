package com.sunchaser.shushan.rpc.core.common;

/**
 * 常量
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/8/16
 */
public interface Constants {

    String NETTY = "netty";

    String UNDERLINE = "_";

    String EMPTY = "";

    /**
     * 0000 0100
     * version: 0000 01
     * type: 00
     */
    byte DEFAULT_PROTOCOL_VERSION = 0x4;

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
}
