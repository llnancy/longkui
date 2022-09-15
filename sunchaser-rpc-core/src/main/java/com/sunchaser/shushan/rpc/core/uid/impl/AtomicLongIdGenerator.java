package com.sunchaser.shushan.rpc.core.uid.impl;

import com.sunchaser.shushan.rpc.core.uid.SequenceIdGenerator;

import java.util.concurrent.atomic.AtomicLong;

/**
 * an id generator implementation based on AtomicLong
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/15
 */
public class AtomicLongIdGenerator implements SequenceIdGenerator {

    private static final AtomicLong SEQUENCE_ID_GENERATOR = new AtomicLong(0);

    /**
     * 获取下一个SequenceId
     *
     * @return long
     */
    @Override
    public long nextSequenceId() {
        return SEQUENCE_ID_GENERATOR.incrementAndGet();
    }
}
