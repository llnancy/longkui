package com.sunchaser.shushan.rpc.core.uid;

import com.sunchaser.shushan.rpc.core.extension.SPI;

/**
 * sequence id generator
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/15
 */
@SPI
public interface SequenceIdGenerator {

    /**
     * 获取下一个SequenceId
     *
     * @return long
     */
    long nextSequenceId();
}
