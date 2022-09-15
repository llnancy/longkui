package com.sunchaser.shushan.rpc.core.compress;

/**
 * Compressor Enum
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/15
 */
public enum CompressorEnum {

    /**
     * 压缩算法枚举
     */
    NONE, SNAPPY, DEFLATE, GZIP, BZIP2, LZ4, LZO
}
