package com.sunchaser.shushan.rpc.core.uid.impl;

import cn.hutool.core.lang.Snowflake;
import com.sunchaser.shushan.rpc.core.uid.SequenceIdGenerator;

/**
 * an id generator implementation based on Snowflake
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/16
 */
public class SnowflakeIdGenerator implements SequenceIdGenerator {

    private final Snowflake snowflake;

    public SnowflakeIdGenerator() {
        this(0, 0);
    }

    public SnowflakeIdGenerator(long workerId, long dataCenterId) {
        this.snowflake = new Snowflake(workerId, dataCenterId);
    }

    /**
     * 获取下一个SequenceId
     *
     * @return long
     */
    @Override
    public long nextSequenceId() {
        return this.snowflake.nextId();
    }
}
