package com.sunchaser.shushan.rpc.core.serialize.impl;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.sunchaser.shushan.rpc.core.serialize.Serializer;
import lombok.SneakyThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Objects;

/**
 * 基于Hessian2的序列化器实现
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/12
 */
public class Hessian2Serializer implements Serializer {

    /**
     * 将对象进行序列化
     *
     * @param obj 待序列化的对象
     * @return 序列化后的byte字节数组
     */
    @SneakyThrows({Throwable.class, Exception.class})
    @Override
    public <T> byte[] serialize(T obj) {
        Hessian2Output output = null;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            output = new Hessian2Output(bos);
            output.writeObject(obj);
            output.flush();
            return bos.toByteArray();
        } finally {
            if (Objects.nonNull(output)) {
                output.close();
            }
        }
    }

    /**
     * 将二进制字节数组进行反序列化
     *
     * @param data  二进制字节数组
     * @param clazz 待反序列化的class类型
     * @return 反序列化后的对象
     */
    @SneakyThrows({Throwable.class, Exception.class})
    @Override
    @SuppressWarnings("unchecked")
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        Hessian2Input input = null;
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data)) {
            input = new Hessian2Input(bis);
            return (T) input.readObject(clazz);
        } finally {
            if (Objects.nonNull(input)) {
                input.close();
            }
        }
    }
}
