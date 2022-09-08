package com.sunchaser.shushan.rpc.core.test.serialize;

import com.sunchaser.shushan.rpc.core.serialize.Serializer;
import com.sunchaser.shushan.rpc.core.serialize.impl.JsonSerializer;

/**
 * JsonSerializer Test
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/8
 */
public class JsonSerializerTest extends AbstractSerializerTest {

    @Override
    protected Serializer getSerializer() {
        return new JsonSerializer();
    }
}
