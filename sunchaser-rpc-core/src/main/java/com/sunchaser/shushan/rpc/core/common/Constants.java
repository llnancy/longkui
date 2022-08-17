package com.sunchaser.shushan.rpc.core.common;

/**
 * 常量
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/8/16
 */
public interface Constants {

    int DEFAULT_CONNECTION_TIMEOUT = 3000;

    int DEFAULT_IO_THREADS = Math.min(Runtime.getRuntime().availableProcessors() + 1, 32);

    int DEFAULT_PORT = 1234;
}
