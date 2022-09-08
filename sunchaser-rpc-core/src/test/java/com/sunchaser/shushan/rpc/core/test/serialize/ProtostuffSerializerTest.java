package com.sunchaser.shushan.rpc.core.test.serialize;

import com.sunchaser.shushan.rpc.core.serialize.Serializer;
import com.sunchaser.shushan.rpc.core.serialize.impl.ProtostuffSerializer;
import lombok.extern.slf4j.Slf4j;

/**
 * ProtostuffSerializer Test
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/19
 */
@Slf4j
class ProtostuffSerializerTest extends AbstractSerializerTest {

    @Override
    protected Serializer getSerializer() {
        return new ProtostuffSerializer();
    }
}