package com.sunchaser.rpc.core.serialize.impl;

import com.sunchaser.rpc.core.serialize.Serializer;

/**
 * 基于XML的序列化器实现
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/13
 */
public class XmlSerializer implements Serializer {

    /**
     * 将对象进行序列化
     *
     * @param obj 待序列化的对象
     * @return 序列化后的byte字节数组
     */
    @Override
    public <T> byte[] serialize(T obj) {
        return new byte[0];
    }

    /**
     * 将二进制字节数组进行反序列化
     *
     * @param data  二进制字节数组
     * @param clazz 待反序列化的class类型
     * @return 反序列化后的对象
     */
    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        return null;
    }
}
