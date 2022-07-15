package com.sunchaser.rpc.core.common;

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

    /**
     * 0000 0100
     * version: 0000 01
     * type: 00
     */
    public static final byte PROTOCOL_HEADER = 0x4;

    /**
     * 0000 0000
     * serialize: Hessian2
     * compress: Snappy
     */
    public static final byte PROTOCOL_INFO = 0x0;

    public static final int HEARTBEAT_CODE = -1;

    public static final long DEFAULT_TIMEOUT = 5L;

    public static boolean isHeartbeat(byte protocolHeader) {
        return (protocolHeader & 3) == 2;
    }

    /**
     * 转换请求类型协议头为响应类型协议头
     * 或运算：如果相对应位都是 0，则结果为 0，否则为 1
     *
     * @param protocolHeader 请求类型协议头
     * @return 响应类型协议头
     */
    public static byte transformToResponse(byte protocolHeader) {
        return (byte) (protocolHeader | 1);
    }
}
