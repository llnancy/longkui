package com.sunchaser.shushan.rpc.core.common;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.Optional;

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
     * 心跳
     */
    HEARTBEAT((byte) 0),

    /**
     * 请求
     */
    REQUEST((byte) 1),

    /**
     * 响应
     */
    RESPONSE((byte) 2),

    ;

    private final byte code;

    private static final Map<Byte, RpcMessageTypeEnum> ENUM_MAP = Maps.newHashMap();

    static {
        for (RpcMessageTypeEnum rpcMessageTypeEnum : RpcMessageTypeEnum.values()) {
            ENUM_MAP.put(rpcMessageTypeEnum.code, rpcMessageTypeEnum);
        }
    }

    public static RpcMessageTypeEnum match(byte code) {
        return Optional.ofNullable(ENUM_MAP.get(code)).orElse(HEARTBEAT);
    }

    public static boolean isHeartbeat(byte code) {
        return code == HEARTBEAT.code;
    }
}
