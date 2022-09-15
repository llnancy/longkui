package com.sunchaser.shushan.rpc.core.common;

/**
 * RPC 上下文
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/12
 */
public class RpcContext {

    public static final int HEADER_SIZE = 15;

    /**
     * 1101110
     */
    public static final byte MAGIC = 0x6E;

    public static final String PING = "ping";

    public static final String PONG = "pong";
}
