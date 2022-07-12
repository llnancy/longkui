package com.sunchaser.rpc.core.common;

/**
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/12
 */
public class RpcContext {

    public static final short MAGIC = (short) 0xE0F1;

    public static final byte VERSION = 1;

    public static final int HEARTBEAT_CODE = -1;

    public static final long DEFAULT_TIMEOUT = 5L;
}
