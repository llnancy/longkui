package com.sunchaser.shushan.rpc.core.test.compress;

import com.sunchaser.shushan.rpc.core.compress.Compressor;
import com.sunchaser.shushan.rpc.core.compress.impl.Bzip2Compressor;

/**
 * Bzip2Compressor Test
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/8
 */
public class Bzip2CompressorTest extends AbstractCompressorTest {

    @Override
    protected Compressor getCompressor() {
        return new Bzip2Compressor();
    }
}
