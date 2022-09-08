package com.sunchaser.shushan.rpc.core.codec;

import com.sunchaser.shushan.rpc.core.common.RpcContext;
import com.sunchaser.shushan.rpc.core.common.RpcMessageTypeEnum;
import com.sunchaser.shushan.rpc.core.compress.Compressor;
import com.sunchaser.shushan.rpc.core.compress.CompressorFactory;
import com.sunchaser.shushan.rpc.core.protocol.RpcHeader;
import com.sunchaser.shushan.rpc.core.protocol.RpcProtocol;
import com.sunchaser.shushan.rpc.core.protocol.RpcRequest;
import com.sunchaser.shushan.rpc.core.protocol.RpcResponse;
import com.sunchaser.shushan.rpc.core.serialize.ArrayElement;
import com.sunchaser.shushan.rpc.core.serialize.Serializer;
import com.sunchaser.shushan.rpc.core.serialize.SerializerFactory;
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

/**
 * 编解码
 *
 * @author sunchaser admin@lilu.org.cn
 * @see RpcHeader
 * @since JDK8 2022/7/13
 */
public class RpcCodec<T> extends ByteToMessageCodec<RpcProtocol<T>> {

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcProtocol<T> msg, ByteBuf out) {
        RpcHeader rpcHeader = msg.getRpcHeader();
        byte compressAndSerialize = rpcHeader.getCompressAndSerialize();
        out.writeByte(rpcHeader.getMagic());
        out.writeByte(rpcHeader.getVersionAndType());
        out.writeByte(compressAndSerialize);
        out.writeLong(rpcHeader.getSequenceId());
        T content = msg.getContent();
        Compressor compressor = CompressorFactory.getCompressor(compressAndSerialize);
        Serializer serializer = SerializerFactory.getSerializer(compressAndSerialize);
        // 序列化后压缩
        byte[] data = compressor.compress(serializer.serialize(content));
        out.writeInt(data.length);
        out.writeBytes(data);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        // 不足消息头长度15个字节，暂不读取
        if (in.readableBytes() < RpcContext.HEADER_SIZE) {
            return;
        }
        // 标记读指针位置
        in.markReaderIndex();
        byte magic = in.readByte();
        if (magic != RpcContext.MAGIC) {
            // 魔数不匹配，重置读指针
            in.resetReaderIndex();
            throw new IllegalArgumentException("magic: " + magic + " is illegal.");
        }
        // 版本号+消息类型
        byte versionAndType = in.readByte();
        // 压缩+序列化方式
        byte compressAndSerialize = in.readByte();
        // 序列ID
        long sequenceId = in.readLong();
        // 消息体长度
        int bodyLength = in.readInt();
        if (in.readableBytes() < bodyLength) {
            // 可读的数据长度小于消息体长度，丢弃此次读取并重置读指针位置
            in.resetReaderIndex();
            return;
        }
        // 读取bodyLength个字节
        byte[] data = new byte[bodyLength];
        in.readBytes(data);
        RpcHeader rpcHeader = RpcHeader.builder()
                .magic(magic)
                .versionAndType(versionAndType)
                .compressAndSerialize(compressAndSerialize)
                .sequenceId(sequenceId)
                .bodyLength(bodyLength)
                .build();
        // 根据消息类型执行不同的解码策略
        RpcMessageDecoderEnum.match(RpcMessageTypeEnum.match(versionAndType))
                .decode(compressAndSerialize, rpcHeader, data, out);
    }

    @AllArgsConstructor
    @Getter
    enum RpcMessageDecoderEnum {

        /**
         * 请求消息
         */
        REQUEST_DECODER(RpcMessageTypeEnum.REQUEST) {
            @Override
            public void decode(byte compressAndSerialize, RpcHeader rpcHeader, byte[] data, List<Object> out) {
                RpcProtocol<RpcRequest> rpcProtocol = buildRpcMessage(compressAndSerialize, rpcHeader, data, RpcRequest.class);
                // kryo、protostuff等序列化框架在序列化时为避免错误用特殊值代替了数组中间索引的null，这里将特殊值还原成null。
                ArrayElement.unwrapArgs(rpcProtocol.getContent().getArgs());
                out.add(rpcProtocol);
            }
        },

        /**
         * 响应消息
         */
        RESPONSE_DECODER(RpcMessageTypeEnum.RESPONSE) {
            @Override
            public void decode(byte compressAndSerialize, RpcHeader rpcHeader, byte[] data, List<Object> out) {
                RpcProtocol<RpcResponse> rpcProtocol = buildRpcMessage(compressAndSerialize, rpcHeader, data, RpcResponse.class);
                out.add(rpcProtocol);
            }
        },

        /**
         * 心跳消息
         */
        HEARTBEAT_DECODER(RpcMessageTypeEnum.HEARTBEAT) {
            @Override
            public void decode(byte compressAndSerialize, RpcHeader rpcHeader, byte[] data, List<Object> out) {
                RpcProtocol<String> rpcProtocol = buildRpcMessage(compressAndSerialize, rpcHeader, data, String.class);
                out.add(rpcProtocol);
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

        public abstract void decode(byte compressAndSerialize,
                                    RpcHeader rpcHeader,
                                    byte[] data,
                                    List<Object> out);

        <I> RpcProtocol<I> buildRpcMessage(byte compressAndSerialize,
                                           RpcHeader rpcHeader,
                                           byte[] data,
                                           Class<I> clazz) {
            Compressor compressor = CompressorFactory.getCompressor(compressAndSerialize);
            Serializer serializer = SerializerFactory.getSerializer(compressAndSerialize);
            // 解压缩后反序列化
            I content = serializer.deserialize(compressor.unCompress(data), clazz);
            return RpcProtocol.<I>builder()
                    .rpcHeader(rpcHeader)
                    .content(content)
                    .build();
        }
    }
}
