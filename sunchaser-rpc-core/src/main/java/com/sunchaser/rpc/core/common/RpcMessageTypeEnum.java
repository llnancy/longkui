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

    REQUEST,

    RESPONSE,

    HEARTBEAT,
    ;

    /**
     * 00：REQUEST
     * 01：RESPONSE
     * 11：HEARTBEAT
     */
    public static RpcMessageTypeEnum match(byte protocolHeader) {
        if ((protocolHeader & 3) == 0) {
            return REQUEST;
        } else if ((protocolHeader & 3) == 1) {
            return RESPONSE;
        } else if (RpcContext.isHeartbeat(protocolHeader)) {
            return HEARTBEAT;
        } else {
            throw new IllegalArgumentException("protocolHeader " + protocolHeader + " is illegal.");
        }
    }
}
