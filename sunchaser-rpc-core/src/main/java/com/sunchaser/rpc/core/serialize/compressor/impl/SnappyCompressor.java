package com.sunchaser.rpc.core.serialize.compressor.impl;

import com.sunchaser.rpc.core.serialize.compressor.Compressor;
import org.xerial.snappy.Snappy;

import java.io.IOException;
import java.util.Objects;

/**
 * 基于Snappy算法实现的压缩与解压缩
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/12
 */
public class SnappyCompressor implements Compressor {

    @Override
    public byte[] compress(byte[] data) throws IOException {
        if (Objects.isNull(data)) {
            return null;
        }
        return Snappy.compress(data);
    }

    @Override
    public byte[] unCompress(byte[] data) throws IOException {
        if (Objects.isNull(data)) {
            return null;
        }
        return Snappy.uncompress(data);
    }
}
