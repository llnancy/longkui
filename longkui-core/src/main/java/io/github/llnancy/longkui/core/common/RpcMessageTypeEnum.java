/*
 * Copyright 2023 LongKui
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Copyright 2022 LongKui
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.llnancy.longkui.core.common;

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
    RESPONSE((byte) 2);

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
