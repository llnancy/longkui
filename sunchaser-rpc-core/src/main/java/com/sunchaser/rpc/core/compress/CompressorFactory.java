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
     * 0001 0000 Snappy
     * 0010 0000 DEFLATE
     * 0011 0000 Gzip
     * 0100 0000 bzip2
     * 0101 0000 LZ4
     * 0110 0000 LZO
     * ......
     * <p>
     * 1111 0000 => 取反 0000 1111 => 加一 0001 0000 => -16 => 0xF0
     * <p>
     * 0000 0000
     * 0001 0000
     * 0010 0000
     * 0011 0000
     * 0100 0000
     * 0101 0000
     * 0110 0000
     * ......
     */
    public static Compressor getCompressor(byte compressAndSerialize) {
        return CompressorEnum.match((byte) (compressAndSerialize & 0xF)).getCompressor();
    }

    @Getter
    @AllArgsConstructor
    enum CompressorEnum {

        /**
         * None
         */
        NONE((byte) 0x0, new NoneCompressor()),

        /**
         * Snappy算法
         */
        SNAPPY((byte) 0x10, new DeflateCompressor()),

        /**
         * DEFLATE算法
         */
        DEFLATE((byte) 0x20, new GzipCompressor()),

        /**
         * Gzip算法
         */
        GZIP((byte) 0x30, new Bzip2Compressor()),

        /**
         * bzip2算法
         */
        BZIP2((byte) 0x40, new Lz4Compressor()),

        /**
         * LZ4算法
         */
        LZ4((byte) 0x50, new LzoCompressor()),

        /**
         * LZO算法
         */
        LZO((byte) 0x60, new LzoCompressor()),

        ;

        private final byte val;

        private final Compressor compressor;

        private static final Map<Byte, CompressorEnum> ENUM_MAP = Maps.newHashMap();

        static {
            for (CompressorEnum compressorEnum : CompressorEnum.values()) {
                ENUM_MAP.put(compressorEnum.val, compressorEnum);
            }
        }

        public static CompressorEnum match(byte val) {
            return Optional.ofNullable(ENUM_MAP.get(val))
                    .orElse(SNAPPY);
        }
    }
}
