package com.sunchaser.shushan.rpc.core.test.serialize.impl;

import com.sunchaser.shushan.rpc.core.protocol.RpcRequest;
import com.sunchaser.shushan.rpc.core.serialize.Serializer;
import com.sunchaser.shushan.rpc.core.serialize.impl.KryoSerializer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * KryoSerializer Test
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/19
 */
@Slf4j
class KryoSerializerTest {

    private static final RpcRequest REQUEST = RpcRequest.builder()
            .serviceName("A")
            .methodName("b")
            .version("1")
            .build();

    @Test
    void serialize() {
        Serializer serializer = new KryoSerializer();
        byte[] serialize = serializer.serialize(REQUEST);
        RpcRequest deserialize = serializer.deserialize(serialize, RpcRequest.class);
        log.info("deserialize: {}", deserialize);
    }
}