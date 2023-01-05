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

package io.github.llnancy.longkui.core.protocol;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * rpc 消息头
 * <p>
 * +------------------------------------------------------------------------+
 * | Bit位 | 0-7 |   8-15  |  16-23 |   24-31  |  32-39  | 40-103 | 104-135 |
 * +------------------------------------------------------------------------+
 * | 协议头 | 魔数 | 协议版本 | 消息类型 | 序列化方式 | 压缩方式 | 序列ID  | 消息体长度 |
 * +------------------------------------------------------------------------+
 * | 协议体 |                       消息体（实际发送的数据）                      |
 * +------------------------------------------------------------------------+
 * <p>
 * 可扩展协议
 * +-----------------------------------------------------------------------------------+
 * | Bit位 | 0-7 |   8-15  | 16-23  |   24-31  |  32-39  | 40-103 | 104-135  |  136-167 |
 * +-----------------------------------------------------------------------------------+
 * | 协议头 | 魔数 | 协议版本 | 消息类型 | 序列化方式 | 压缩方式 | 序列ID  | 协议头长度 | 消息体长度 |
 * +-----------------------------------------------------------------------------------+
 * |                       协议头扩展字段（长度不固定，由协议头长度决定）                       |
 * +-----------------------------------------------------------------------------------+
 * |                                消息体（实际发送的数据）                                |
 * +-----------------------------------------------------------------------------------+
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RpcHeader implements Serializable {

    private static final long serialVersionUID = 3050587176252634022L;

    /**
     * 魔数：1101110
     * 天王盖地虎
     * 宝塔镇河妖
     */
    private byte magic;

    /**
     * 协议版本
     */
    private byte version;

    /**
     * 消息类型：
     * 0：HEARTBEAT
     * 1：REQUEST
     * 2：RESPONSE
     */
    private byte type;

    /**
     * 序列化方式
     */
    private byte serialize;

    /**
     * 压缩方式
     */
    private byte compress;

    /**
     * 序列ID
     */
    private long sequenceId;

    /**
     * 消息体长度
     */
    private int bodyLength;
}
