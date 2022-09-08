package com.sunchaser.shushan.rpc.core.test.compress;

import com.sunchaser.shushan.rpc.core.compress.impl.SnappyCompressor;
import lombok.extern.slf4j.Slf4j;

/**
 * SnappyCompressor Test
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/8
 */
@Slf4j
public class SnappyCompressorTest extends AbstractCompressorTest {

    @Override
    protected SnappyCompressor getCompressor() {
        return new SnappyCompressor();
    }
}
