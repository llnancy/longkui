package com.sunchaser.rpc.core.compress.impl;

import com.sunchaser.rpc.core.compress.Compressor;
import lombok.SneakyThrows;
import org.xerial.snappy.Snappy;

import java.util.Objects;

/**
 * 基于Snappy算法实现的压缩与解压缩
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/12
 */
public class SnappyCompressor implements Compressor {

    /**
     * 将数据进行压缩
     *
     * @param data 原比特数组
     * @return 压缩后的数据
     */
    @SneakyThrows
    @Override
    public byte[] compress(byte[] data) {
        Objects.requireNonNull(data);
        return Snappy.compress(data);
    }

    /**
     * 将数据解压缩
     *
     * @param data 压缩的数据
     * @return 原数据
     */
    @SneakyThrows
    @Override
    public byte[] unCompress(byte[] data) {
        Objects.requireNonNull(data);
        return Snappy.uncompress(data);
    }
}
