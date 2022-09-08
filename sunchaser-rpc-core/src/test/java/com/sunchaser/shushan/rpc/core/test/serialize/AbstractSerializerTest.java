package com.sunchaser.shushan.rpc.core.test.serialize;

import com.sunchaser.shushan.rpc.core.protocol.RpcRequest;
import com.sunchaser.shushan.rpc.core.serialize.Serializer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * Abstract Serializer Test
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/8
 */
@Slf4j
public abstract class AbstractSerializerTest {

    protected static final RpcRequest REQUEST = RpcRequest.builder()
            .serviceName("com.sunchaser.shushan.rpc.core.test.HelloService")
            .methodName("sayHello")
            .version("1")
            .argTypes(new Class[]{String.class, null, Integer.class})
            .args(new Object[]{"hello, sunchaser", null, 666})
            .build();

    @Test
    void serialize() {
        Serializer serializer = getSerializer();
        byte[] serialize = serializer.serialize(REQUEST);
        RpcRequest deserialize = serializer.deserialize(serialize, RpcRequest.class);
        LOGGER.info("deserialize: {}", deserialize);
    }

    protected abstract Serializer getSerializer();
}
