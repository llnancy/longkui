package com.sunchaser.rpc.core.serialize.impl;

import com.sunchaser.rpc.core.serialize.Serializer;
import lombok.SneakyThrows;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

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
    @SneakyThrows
    @Override
    public <T> byte[] serialize(T obj) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             XMLEncoder xmlEncoder = new XMLEncoder(bos, StandardCharsets.UTF_8.name(), true, 0)) {
            xmlEncoder.writeObject(obj);
            return bos.toByteArray();
        }
    }

    /**
     * 将二进制字节数组进行反序列化
     *
     * @param data  二进制字节数组
     * @param clazz 待反序列化的class类型
     * @return 反序列化后的对象
     */
    @SneakyThrows
    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
             XMLDecoder xmlDecoder = new XMLDecoder(bis)) {
            Object obj = xmlDecoder.readObject();
            return clazz.cast(obj);
        }
    }
}
