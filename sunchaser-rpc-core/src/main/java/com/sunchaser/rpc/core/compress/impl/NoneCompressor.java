package com.sunchaser.rpc.core.compress.impl;

import com.sunchaser.rpc.core.compress.Compressor;

/**
 * 不进行压缩
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/14
 */
public class NoneCompressor implements Compressor {

    @Override
    public byte[] compress(byte[] data) {
        return data;
    }

    @Override
    public byte[] unCompress(byte[] data) {
        return data;
    }
}
