package com.sunchaser.shushan.rpc.core.serialize.impl;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.util.Pool;
import com.sunchaser.shushan.rpc.core.serialize.Serializer;
import lombok.SneakyThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Objects;

/**
 * 基于Kryo的序列化器实现
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/13
 */
public class KryoSerializer implements Serializer {

    /**
     * 对象池模式
     * 也可使用ThreadLocal来保证线程安全
     *
     * @see com.esotericsoftware.kryo.util.Pool#obtain()
     * @see com.esotericsoftware.kryo.util.Pool#free(java.lang.Object)
     */
    private static final Pool<Kryo> KRYO_POOL = new Pool<Kryo>(true, false, 8) {
        @Override
        protected Kryo create() {
            Kryo kryo = new Kryo();
            // 支持循环引用。默认值为true。勿修改，否则可能造成栈溢出。
            kryo.setReferences(true);
            // 关闭注册行为，不强制要求注册类（注册行为无法保证多个JVM内同一个类的注册编号相同）。默认值为false。勿修改。
            kryo.setRegistrationRequired(false);
            return kryo;
        }
    };

    private static final Pool<Output> OUTPUT_POOL = new Pool<Output>(true, false, 16) {
        @Override
        protected Output create() {
            return new Output(1024, -1);
        }
    };

    private static final Pool<Input> INPUT_POOL = new Pool<Input>(true, false, 16) {
        @Override
        protected Input create() {
            return new Input(1024);
        }
    };

    /**
     * Get type unique id
     *
     * @return content type id
     */
    @Override
    public byte getTypeId() {
        return (byte) 4;
    }

    /**
     * 将对象进行序列化
     *
     * @param obj 待序列化的对象
     * @return 序列化后的byte字节数组
     */
    @SneakyThrows
    @Override
    public <T> byte[] serialize(T obj) {
        Kryo kryo = null;
        Output output = null;
        try {
            output = OUTPUT_POOL.obtain();
            output.setOutputStream(new ByteArrayOutputStream());
            kryo = KRYO_POOL.obtain();
            kryo.writeObject(output, obj);
            return output.toBytes();
        } finally {
            if (Objects.nonNull(kryo)) {
                KRYO_POOL.free(kryo);
            }
            if (Objects.nonNull(output)) {
                OUTPUT_POOL.free(output);
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
    @SneakyThrows
    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        Kryo kryo = null;
        Input input = null;
        try {
            input = INPUT_POOL.obtain();
            input.setInputStream(new ByteArrayInputStream(data));
            kryo = KRYO_POOL.obtain();
            return kryo.readObject(input, clazz);
        } finally {
            if (Objects.nonNull(kryo)) {
                KRYO_POOL.free(kryo);
            }
            if (Objects.nonNull(input)) {
                INPUT_POOL.free(input);
                input.close();
            }
        }
    }
}
