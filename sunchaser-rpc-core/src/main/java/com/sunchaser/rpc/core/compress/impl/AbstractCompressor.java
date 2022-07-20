package com.sunchaser.rpc.core.compress.impl;

import com.sunchaser.rpc.core.compress.Compressor;

import java.util.Objects;

/**
 * an abstract compressor impl
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/20
 */
public abstract class AbstractCompressor implements Compressor {

    /**
     * 将数据进行压缩
     *
     * @param data 原比特数组
     * @return 压缩后的数据
     */
    @Override
    public byte[] compress(byte[] data) {
        Objects.requireNonNull(data, "compress data is null");
        return doCompress(data);
    }

    /**
     * 将数据解压缩
     *
     * @param data 压缩的数据
     * @return 原数据
     */
    @Override
    public byte[] unCompress(byte[] data) {
        Objects.requireNonNull(data, "unCompress data is null");
        return doUnCompress(data);
    }

    /**
     * 将数据进行压缩
     *
     * @param data 原比特数组
     * @return 压缩后的数据
     */
    protected abstract byte[] doCompress(byte[] data);

    /**
     * 将数据解压缩
     *
     * @param data 压缩的数据
     * @return 原数据
     */
    protected abstract byte[] doUnCompress(byte[] data);
}
