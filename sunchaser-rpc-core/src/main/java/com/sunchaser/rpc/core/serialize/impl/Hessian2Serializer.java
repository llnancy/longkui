package com.sunchaser.rpc.core.serialize.impl;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.sunchaser.rpc.core.serialize.Serializer;
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

    @SneakyThrows
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

    @SneakyThrows
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
