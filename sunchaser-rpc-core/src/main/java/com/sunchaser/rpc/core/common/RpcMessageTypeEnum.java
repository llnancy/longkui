package com.sunchaser.rpc.core.common;

import com.sunchaser.rpc.core.protocol.RpcHeader;
import com.sunchaser.rpc.core.protocol.RpcMessage;
import com.sunchaser.rpc.core.protocol.RpcRequest;
import com.sunchaser.rpc.core.protocol.RpcResponse;
import com.sunchaser.rpc.core.compress.Compressor;
import com.sunchaser.rpc.core.compress.CompressorFactory;
import com.sunchaser.rpc.core.serialize.SerializerFactory;
import com.sunchaser.rpc.core.serialize.Serializer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

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

    REQUEST() {
        @Override
        public void invoke(byte protocolInfo, RpcHeader rpcHeader, byte[] data, List<Object> out) throws Exception {
            RpcMessage<RpcRequest> rpcMessage = buildRpcMessage(protocolInfo, rpcHeader, data, RpcRequest.class);
            out.add(rpcMessage);
        }
    },

    RESPONSE() {
        @Override
        public void invoke(byte protocolInfo, RpcHeader rpcHeader, byte[] data, List<Object> out) throws Exception {
            RpcMessage<RpcResponse> rpcMessage = buildRpcMessage(protocolInfo, rpcHeader, data, RpcResponse.class);
            out.add(rpcMessage);
        }
    },

    HEARTBEAT() {
        @Override
        public void invoke(byte protocolInfo, RpcHeader rpcHeader, byte[] data, List<Object> out) throws Exception {

        }
    },
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

    <I> RpcMessage<I> buildRpcMessage(byte protocolInfo,
                                      RpcHeader rpcHeader,
                                      byte[] data,
                                      Class<I> clazz) throws Exception {
        Serializer serializer = SerializerFactory.getSerializer(protocolInfo);
        Compressor compressor = CompressorFactory.getCompressor(protocolInfo);
        I content = serializer.deserialize(compressor.unCompress(data), clazz);
        return RpcMessage.<I>builder()
                .rpcHeader(rpcHeader)
                .content(content)
                .build();
    }

    public abstract void invoke(byte protocolInfo,
                                RpcHeader rpcHeader,
                                byte[] data,
                                List<Object> out) throws Exception;
}
