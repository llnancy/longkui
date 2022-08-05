package com.sunchaser.rpc.core.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * RPC 消息类型枚举
 * <p>
 * REQUEST：请求
 * RESPONSE：响应
 * HEARTBEAT：心跳
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/13
 */
@AllArgsConstructor
@Getter
public enum RpcMessageTypeEnum {

    /**
     * 请求
     */
    REQUEST,

    /**
     * 响应
     */
    RESPONSE,

    /**
     * 心跳
     */
    HEARTBEAT,

    ;

    private static final byte MESSAGE_TYPE_FACTOR = 3;

    /**
     * 00：REQUEST
     * 01：RESPONSE
     * 11：HEARTBEAT
     */
    public static RpcMessageTypeEnum match(byte versionAndType) {
        if ((versionAndType & MESSAGE_TYPE_FACTOR) == 0) {
            return REQUEST;
        } else if ((versionAndType & MESSAGE_TYPE_FACTOR) == 1) {
            return RESPONSE;
        } else if (RpcContext.isHeartbeat(versionAndType)) {
            return HEARTBEAT;
        } else {
            throw new IllegalArgumentException("versionAndType " + versionAndType + " is illegal.");
        }
    }
}
