package com.sunchaser.rpc.core.serialize.impl;

import com.sunchaser.rpc.core.serialize.Serializer;

/**
 * 基于XML的序列化器实现
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/13
 */
public class XmlSerializer implements Serializer {

    @Override
    public <T> byte[] serialize(T obj) {
        return new byte[0];
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        return null;
    }
}
