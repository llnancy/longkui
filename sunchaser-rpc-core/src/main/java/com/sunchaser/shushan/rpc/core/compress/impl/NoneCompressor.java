package com.sunchaser.shushan.rpc.core.compress.impl;

/**
 * 不进行压缩
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/14
 */
public class NoneCompressor extends AbstractCompressor {

    /**
     * 将数据进行压缩
     *
     * @param data 原比特数组
     * @return 压缩后的数据
     */
    @Override
    protected byte[] doCompress(byte[] data) {
        return data;
    }

    /**
     * 将数据解压缩
     *
     * @param data 压缩的数据
     * @return 原数据
     */
    @Override
    protected byte[] doUnCompress(byte[] data) {
        return data;
    }
}
