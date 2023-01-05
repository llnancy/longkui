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

package io.github.llnancy.longkui.core.handler;

import com.google.common.collect.Maps;
import io.github.llnancy.longkui.core.protocol.RpcFuture;
import io.github.llnancy.longkui.core.protocol.RpcResponse;

import java.util.concurrent.ConcurrentMap;

/**
 * Rpc pending Holder
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/15
 */
public class RpcPendingHolder {

    /**
     * todo 时间轮算法定时清理
     */
    private static final ConcurrentMap<Long, RpcFuture<RpcResponse>> RPC_FUTURE_MAP = Maps.newConcurrentMap();

    public static void putRpcFuture(Long sequenceId, RpcFuture<RpcResponse> rpcFuture) {
        RPC_FUTURE_MAP.put(sequenceId, rpcFuture);
    }

    public static RpcFuture<RpcResponse> removeRpcFuture(Long sequenceId) {
        return RPC_FUTURE_MAP.remove(sequenceId);
    }

    public static RpcFuture<RpcResponse> getRpcFuture(Long sequenceId) {
        return RPC_FUTURE_MAP.get(sequenceId);
    }
}
