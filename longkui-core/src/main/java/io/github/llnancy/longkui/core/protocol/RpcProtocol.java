/*
 * Copyright 2022 LongKui
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

package io.github.llnancy.longkui.core.protocol;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * rpc 消息协议
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/6/28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RpcProtocol<T> implements Serializable {

    private static final long serialVersionUID = 3022652409163567503L;

    /**
     * 消息头
     */
    private RpcHeader rpcHeader;

    /**
     * 消息体
     */
    private T rpcBody;
}
