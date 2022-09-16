package com.sunchaser.shushan.rpc.core.compress;

import com.sunchaser.shushan.rpc.core.extension.SPI;
import com.sunchaser.shushan.rpc.core.util.TypeId;

/**
 * 压缩器
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/12
 */
@SPI
public interface Compressor extends TypeId {

    /**
     * 将数据进行压缩
     *
     * @param data 原比特数组
     * @return 压缩后的数据
     */
    byte[] compress(byte[] data);

    /**
     * 将数据解压缩
     *
     * @param data 压缩的数据
     * @return 原数据
     */
    byte[] unCompress(byte[] data);
}
