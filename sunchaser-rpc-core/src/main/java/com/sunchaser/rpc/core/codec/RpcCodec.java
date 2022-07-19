package com.sunchaser.rpc.core.codec;

import com.sunchaser.rpc.core.common.RpcContext;
import com.sunchaser.rpc.core.common.RpcMessageTypeEnum;
import com.sunchaser.rpc.core.compress.Compressor;
import com.sunchaser.rpc.core.compress.CompressorFactory;
import com.sunchaser.rpc.core.protocol.RpcHeader;
import com.sunchaser.rpc.core.protocol.RpcProtocol;
import com.sunchaser.rpc.core.protocol.RpcRequest;
import com.sunchaser.rpc.core.protocol.RpcResponse;
import com.sunchaser.rpc.core.serialize.ArrayElement;
import com.sunchaser.rpc.core.serialize.Serializer;
import com.sunchaser.rpc.core.serialize.SerializerFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.sunchaser.rpc.core.common.RpcMessageTypeEnum.*;

/**
 * 编解码
 *
 * @author sunchaser admin@lilu.org.cn
 * @see RpcHeader
 * @since JDK8 2022/7/13
 */
public class RpcCodec<T> extends ByteToMessageCodec<RpcProtocol<T>> {

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcProtocol<T> msg, ByteBuf out) throws Exception {
        RpcHeader rpcHeader = msg.getRpcHeader();
        byte protocolHeader = rpcHeader.getProtocolHeader();
        byte protocolInfo = rpcHeader.getProtocolInfo();
        out.writeByte(rpcHeader.getMagic());
        out.writeByte(protocolHeader);
        out.writeByte(protocolInfo);
        out.writeLong(rpcHeader.getSequenceId());
        T content = msg.getContent();
        if (RpcContext.isHeartbeat(protocolHeader)) {// 心跳消息，无消息体
            out.writeInt(0);
            return;
        }
        Serializer serializer = SerializerFactory.getSerializer(protocolInfo);
        Compressor compressor = CompressorFactory.getCompressor(protocolInfo);
        byte[] data = compressor.compress(serializer.serialize(content));
        out.writeInt(data.length);
        out.writeBytes(data);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < RpcContext.HEADER_SIZE) {// 不足消息头长度15字节，暂不读取
            return;
        }
        in.markReaderIndex();
        byte magic = in.readByte();
        if (magic != RpcContext.MAGIC) {
            in.resetReaderIndex();
            throw new IllegalArgumentException("magic: " + magic + " is illegal.");
        }
        byte protocolHeader = in.readByte();
        byte protocolInfo = in.readByte();
        long sequenceId = in.readLong();
        int length = in.readInt();
        if (in.readableBytes() < length) {
            // 可读的数据长度小于消息体长度，丢弃此次读取并重置读指针位置
            in.resetReaderIndex();
            return;
        }
        byte[] data = new byte[length];
        in.readBytes(data);
        RpcHeader rpcHeader = RpcHeader.builder()
                .magic(magic)
                .protocolHeader(protocolHeader)
                .protocolInfo(protocolInfo)
                .sequenceId(sequenceId)
                .length(length)
                .build();
        RpcMessageDecoderEnum.match(RpcMessageTypeEnum.match(protocolHeader))
                .decode(protocolInfo, rpcHeader, data, out);
    }

    @AllArgsConstructor
    @Getter
    enum RpcMessageDecoderEnum {

        REQUEST_DECODER(REQUEST) {
            @Override
            public void decode(byte protocolInfo, RpcHeader rpcHeader, byte[] data, List<Object> out) {
                RpcProtocol<RpcRequest> rpcProtocol = buildRpcMessage(protocolInfo, rpcHeader, data, RpcRequest.class);
                // kryo、protostuff等序列化框架在序列化时为避免错误用特殊值代替了数组中间索引的null，这里将特殊值还原成null。
                ArrayElement.unwrapArgs(rpcProtocol.getContent().getArgs());
                out.add(rpcProtocol);
            }
        },

        RESPONSE_DECODER(RESPONSE) {
            @Override
            public void decode(byte protocolInfo, RpcHeader rpcHeader, byte[] data, List<Object> out) {
                RpcProtocol<RpcResponse> rpcProtocol = buildRpcMessage(protocolInfo, rpcHeader, data, RpcResponse.class);
                out.add(rpcProtocol);
            }
        },

        HEARTBEAT_DECODER(HEARTBEAT) {
            @Override
            public void decode(byte protocolInfo, RpcHeader rpcHeader, byte[] data, List<Object> out) {
                // TODO
            }
        }

        ;

        private final RpcMessageTypeEnum rpcType;

        private static final Map<RpcMessageTypeEnum, RpcMessageDecoderEnum> ENUM_MAP;

        static {
            ENUM_MAP = Arrays.stream(RpcMessageDecoderEnum.values())
                    .collect(Collectors.toMap(RpcMessageDecoderEnum::getRpcType, Function.identity()));
        }

        public static RpcMessageDecoderEnum match(RpcMessageTypeEnum rpcType) {
            return Optional.ofNullable(ENUM_MAP.get(rpcType))
                    .orElse(HEARTBEAT_DECODER);
        }

        public abstract void decode(byte protocolInfo,
                                    RpcHeader rpcHeader,
                                    byte[] data,
                                    List<Object> out);

        <I> RpcProtocol<I> buildRpcMessage(byte protocolInfo,
                                           RpcHeader rpcHeader,
                                           byte[] data,
                                           Class<I> clazz) {
            Serializer serializer = SerializerFactory.getSerializer(protocolInfo);
            Compressor compressor = CompressorFactory.getCompressor(protocolInfo);
            I content = serializer.deserialize(compressor.unCompress(data), clazz);
            System.out.println("deserialize " + content);
            return RpcProtocol.<I>builder()
                    .rpcHeader(rpcHeader)
                    .content(content)
                    .build();
        }
    }
}
