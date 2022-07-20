package com.sunchaser.rpc.core.compress;

import com.google.common.collect.Maps;
import com.sunchaser.rpc.core.compress.impl.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.Optional;

/**
 * Compressor Factory（简单工厂模式）
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/14
 */
public class CompressorFactory {

    /**
     * 0000 0000 None
     * 0000 0001 Snappy
     * 0000 0010 DEFLATE
     * 0000 0011 Gzip
     * 0000 0100 bzip2
     * 0000 0101 LZ4
     * 0000 0110 LZO
     * ......
     * <p>
     * 0000 1111 => 15 => 0xF
     * <p>
     * 0000 0000
     * 0000 0001
     * 0000 0010
     * 0000 0011
     * 0000 0100
     * 0000 0101
     * 0000 0110
     * ......
     */
    public static Compressor getCompressor(byte protocolInfo) {
        return CompressorEnum.match((byte) (protocolInfo & 0xF)).getCompressor();
    }

    @Getter
    @AllArgsConstructor
    enum CompressorEnum {

        NONE((byte) 0x0, new NoneCompressor()),

        SNAPPY((byte) 0x1, new DeflateCompressor()),

        DEFLATE((byte) 0x2, new GzipCompressor()),

        GZIP((byte) 0x3, new Bzip2Compressor()),

        BZIP2((byte) 0x4, new Lz4Compressor()),

        LZ4((byte) 0x5, new LzoCompressor()),

        LZO((byte) 0x6, new LzoCompressor()),
        ;

        private final byte val;

        private final Compressor compressor;

        private static final Map<Byte, CompressorEnum> enumMap = Maps.newHashMap();

        static {
            for (CompressorEnum compressorEnum : CompressorEnum.values()) {
                enumMap.put(compressorEnum.val, compressorEnum);
            }
        }

        public static CompressorEnum match(byte protocolInfo) {
            return Optional.ofNullable(enumMap.get(protocolInfo))
                    .orElse(SNAPPY);
        }
    }
}
