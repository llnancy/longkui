package com.sunchaser.rpc.core.serialize.serializator.impl;

import com.sunchaser.rpc.core.serialize.serializator.Serializer;

import java.io.IOException;

/**
 * 基于Protostuff的序列化器实现
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/13
 */
public class ProtostuffSerializer implements Serializer {

    @Override
    public <T> byte[] serialize(T obj) throws IOException {
        return new byte[0];
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) throws IOException {
        return null;
    }
}
