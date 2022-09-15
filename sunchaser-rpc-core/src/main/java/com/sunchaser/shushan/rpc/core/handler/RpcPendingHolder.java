package com.sunchaser.shushan.rpc.core.handler;

import com.google.common.collect.Maps;
import com.sunchaser.shushan.rpc.core.protocol.RpcFuture;
import com.sunchaser.shushan.rpc.core.protocol.RpcResponse;

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
}
