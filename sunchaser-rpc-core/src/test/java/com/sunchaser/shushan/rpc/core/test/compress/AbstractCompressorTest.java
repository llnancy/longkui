package com.sunchaser.shushan.rpc.core.test.compress;

import com.sunchaser.shushan.rpc.core.compress.Compressor;
import com.sunchaser.shushan.rpc.core.protocol.RpcRequest;
import com.sunchaser.shushan.rpc.core.serialize.Serializer;
import com.sunchaser.shushan.rpc.core.serialize.impl.JsonSerializer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * Abstract Compressor Test
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/8
 */
@Slf4j
public abstract class AbstractCompressorTest {

    protected static final RpcRequest REQUEST = RpcRequest.builder()
            .serviceName("com.sunchaser.shushan.rpc.core.test.HelloService")
            .methodName("sayHello")
            .version("1")
            .argTypes(new Class[]{String.class, null, Integer.class})
            .args(new Object[]{"hello, sunchaser", null, 666})
            .build();

    @Test
    public void compress() {
        Serializer serializer = new JsonSerializer();
        byte[] serialize = serializer.serialize(REQUEST);
        Compressor compressor = getCompressor();
        byte[] compress = compressor.compress(serialize);
        byte[] unCompress = compressor.unCompress(compress);
        RpcRequest deserialize = serializer.deserialize(unCompress, RpcRequest.class);
        LOGGER.info("deserialize: {}", deserialize);
    }

    protected abstract Compressor getCompressor();
}
