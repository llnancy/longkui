package com.sunchaser.rpc.core.codec;

import com.sunchaser.rpc.core.common.RpcContext;
import com.sunchaser.rpc.core.common.RpcMessageTypeEnum;
import com.sunchaser.rpc.core.compress.Compressor;
import com.sunchaser.rpc.core.compress.CompressorFactory;
import com.sunchaser.rpc.core.protocol.RpcHeader;
import com.sunchaser.rpc.core.protocol.RpcProtocol;
import com.sunchaser.rpc.core.serialize.Serializer;
import com.sunchaser.rpc.core.serialize.SerializerFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import java.util.List;

/**
 * 编解码
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/13
 * @see RpcHeader
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
        RpcMessageTypeEnum.match(protocolHeader)
                .invoke(protocolInfo, rpcHeader, data, out);
    }
}
