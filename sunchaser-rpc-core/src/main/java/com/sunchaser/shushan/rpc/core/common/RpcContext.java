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

    /**
     * 0000 0100
     * version: 0000 01
     * type: 00
     */
    public static final byte DEFAULT_VERSION_AND_TYPE = 0x4;

    /**
     * 0000 0000
     * compress: Snappy
     * serialize: Hessian2
     */
    public static final byte DEFAULT_COMPRESS_SERIALIZE = 0x0;

    public static final int HEARTBEAT_CODE = -1;

    public static final long DEFAULT_TIMEOUT = 5L;

    public static boolean isHeartbeat(byte versionAndType) {
        return (versionAndType & 3) == 3;
    }

    /**
     * 转换请求类型为响应类型
     * 或运算：如果相对应位都是 0，则结果为 0，否则为 1
     *
     * @param versionAndType 协议版本号+消息请求类型
     * @return 转化为响应类型
     */
    public static byte transformToResponse(byte versionAndType) {
        return (byte) (versionAndType | 1);
    }
}
