package com.sunchaser.shushan.rpc.core.compress.impl;

import com.sunchaser.shushan.rpc.core.util.IoUtils;
import lombok.SneakyThrows;
import org.anarres.lzo.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * 基于lzo（Lempel-Ziv-Oberhumer）算法实现的压缩与解压缩
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/20
 */
public class LzoCompressor extends AbstractCompressor {

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
             LzoOutputStream lzoOs = new LzoOutputStream(bos, LzoLibrary.getInstance().newCompressor(LzoAlgorithm.LZO1X, null))) {
            lzoOs.write(data);
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
             LzoInputStream lzoIs = new LzoInputStream(new ByteArrayInputStream(data), LzoLibrary.getInstance().newDecompressor(LzoAlgorithm.LZO1X, LzoConstraint.SPEED))) {
            IoUtils.copy(lzoIs, bos);
            return bos.toByteArray();
        }
    }
}
