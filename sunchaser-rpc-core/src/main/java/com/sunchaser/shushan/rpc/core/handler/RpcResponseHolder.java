package com.sunchaser.shushan.rpc.core.handler;

import com.google.common.collect.Maps;
import com.sunchaser.shushan.rpc.core.protocol.RpcFuture;
import com.sunchaser.shushan.rpc.core.protocol.RpcResponse;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Rpc Response Holder
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/15
 */
public class RpcResponseHolder {

    private static final AtomicLong SEQUENCE_ID_GENERATOR = new AtomicLong(0);

    private static final ConcurrentMap<Long, RpcFuture<RpcResponse>> RPC_FUTURE_MAP = Maps.newConcurrentMap();

    public static Long generateSequenceId() {
        return SEQUENCE_ID_GENERATOR.incrementAndGet();
    }

    public static void putRpcFuture(Long sequenceId, RpcFuture<RpcResponse> rpcFuture) {
        RPC_FUTURE_MAP.put(sequenceId, rpcFuture);
    }

    public static RpcFuture<RpcResponse> removeRpcFuture(Long sequenceId) {
        return RPC_FUTURE_MAP.remove(sequenceId);
    }
}
