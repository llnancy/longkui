package com.sunchaser.shushan.rpc.core.compress.impl;

import com.sunchaser.shushan.rpc.core.util.IoUtils;
import lombok.SneakyThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * 基于Gzip算法实现的压缩与解压缩
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/15
 */
public class GzipCompressor extends AbstractCompressor {

    /**
     * 将数据进行压缩
     *
     * @param data 原比特数组
     * @return 压缩后的数据
     */
    @SneakyThrows({Throwable.class, Exception.class})
    @Override
    protected byte[] doCompress(byte[] data) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             GZIPOutputStream gzip = new GZIPOutputStream(bos)) {
            gzip.write(data);
            return bos.toByteArray();
        }
    }

    /**
     * 将数据解压缩
     *
     * @param data 压缩的数据
     * @return 原数据
     */
    @SneakyThrows({Throwable.class, Exception.class})
    @Override
    protected byte[] doUnCompress(byte[] data) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             GZIPInputStream unzip = new GZIPInputStream(new ByteArrayInputStream(data))) {
            IoUtils.copy(unzip, bos);
            return bos.toByteArray();
        }
    }
}
