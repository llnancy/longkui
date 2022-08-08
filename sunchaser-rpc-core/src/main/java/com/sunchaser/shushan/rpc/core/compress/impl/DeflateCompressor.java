package com.sunchaser.shushan.rpc.core.compress.impl;

import lombok.SneakyThrows;

import java.io.ByteArrayOutputStream;
import java.util.Objects;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * 基于DEFLATE算法实现的压缩与解压缩
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/20
 */
public class DeflateCompressor extends AbstractCompressor {

    /**
     * 将数据进行压缩
     *
     * @param data 原比特数组
     * @return 压缩后的数据
     */
    @SneakyThrows({Throwable.class, Exception.class})
    @Override
    protected byte[] doCompress(byte[] data) {
        Deflater deflater = null;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            deflater = new Deflater(1);
            deflater.setInput(data);
            deflater.finish();
            final byte[] buffer = new byte[2048];
            while (!deflater.finished()) {
                int len = deflater.deflate(buffer);
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } finally {
            if (Objects.nonNull(deflater)) {
                deflater.end();
            }
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
        Inflater inflater = null;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            inflater = new Inflater();
            inflater.setInput(data);
            final byte[] buffer = new byte[2048];
            while (!inflater.finished()) {
                int len = inflater.inflate(buffer);
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } finally {
            if (Objects.nonNull(inflater)) {
                inflater.end();
            }
        }
    }
}
