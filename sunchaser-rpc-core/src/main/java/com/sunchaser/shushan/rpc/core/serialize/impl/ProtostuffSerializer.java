package com.sunchaser.shushan.rpc.core.serialize.impl;

import com.google.common.collect.Maps;
import com.sunchaser.shushan.rpc.core.serialize.Serializer;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.util.concurrent.ConcurrentMap;

/**
 * 基于Protostuff的序列化器实现
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/13
 */
public class ProtostuffSerializer implements Serializer {

    private static final ConcurrentMap<Class<?>, Schema<?>> SCHEMA_CONCURRENT_MAP = Maps.newConcurrentMap();

    @SuppressWarnings("unchecked")
    private static <T> Schema<T> getSchema(Class<?> clazz) {
        return (Schema<T>) SCHEMA_CONCURRENT_MAP.computeIfAbsent(clazz, k -> RuntimeSchema.getSchema(clazz));
    }

    /**
     * Get content type unique id
     *
     * @return content type id
     */
    @Override
    public byte getContentTypeId() {
        return (byte) 3;
    }

    /**
     * 将对象进行序列化
     *
     * @param obj 待序列化的对象
     * @return 序列化后的byte字节数组
     */
    @Override
    public <T> byte[] serialize(T obj) {
        // 局部变量保证线程安全
        LinkedBuffer buffer = LinkedBuffer.allocate();
        try {
            Schema<T> schema = getSchema(obj.getClass());
            return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        } finally {
            buffer.clear();
        }
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
        Schema<T> schema = getSchema(clazz);
        T obj = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(data, obj, schema);
        return obj;
    }
}
