package com.sunchaser.rpc.core.serialize.factory;

import com.google.common.collect.Maps;
import com.sunchaser.rpc.core.serialize.compressor.Compressor;
import com.sunchaser.rpc.core.serialize.compressor.impl.SnappyCompressor;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.Optional;

/**
 * Compressor Factory
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/12
 */
public class CompressorFactory {

    /**
     * 0000 0000 Snappy
     * 0000 0001 ...
     * ......
     * <p>
     * 0000 1111 => 15 => 0xF
     * <p>
     * 0000 0000
     * ......
     */
    public static Compressor getCompressor(byte protocolInfo) {
        return CompressorEnum.match((byte) (protocolInfo & 0xF)).getCompressor();
    }

    @Getter
    @AllArgsConstructor
    enum CompressorEnum {

        SNAPPY((byte) 0x0, new SnappyCompressor()),
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
