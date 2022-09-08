package com.sunchaser.shushan.rpc.core.test.serialize;

import com.sunchaser.shushan.rpc.core.serialize.Serializer;
import com.sunchaser.shushan.rpc.core.serialize.impl.Hessian2Serializer;
import lombok.extern.slf4j.Slf4j;

/**
 * Hessian2Serializer Test
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/8
 */
@Slf4j
public class Hessian2SerializerTest extends AbstractSerializerTest {

    @Override
    protected Serializer getSerializer() {
        return new Hessian2Serializer();
    }
}
