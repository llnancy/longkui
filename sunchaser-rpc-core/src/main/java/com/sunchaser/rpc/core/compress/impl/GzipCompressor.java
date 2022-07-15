package com.sunchaser.rpc.core.compress.impl;

import com.sunchaser.rpc.core.compress.Compressor;

/**
 * Gzip压缩
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/15
 */
public class GzipCompressor implements Compressor {

    /**
     * 将数据进行压缩
     *
     * @param data 原比特数组
     * @return 压缩后的数据
     */
    @Override
    public byte[] compress(byte[] data) {
        return new byte[0];
    }

    /**
     * 将数据解压缩
     *
     * @param data 压缩的数据
     * @return 原数据
     */
    @Override
    public byte[] unCompress(byte[] data) {
        return new byte[0];
    }
}
