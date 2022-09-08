package com.sunchaser.shushan.rpc.core.test.compress;

import com.sunchaser.shushan.rpc.core.compress.Compressor;
import com.sunchaser.shushan.rpc.core.compress.impl.DeflateCompressor;

/**
 * DeflateCompressor Test
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/8
 */
public class DeflateCompressorTest extends AbstractCompressorTest {

    @Override
    protected Compressor getCompressor() {
        return new DeflateCompressor();
    }
}
