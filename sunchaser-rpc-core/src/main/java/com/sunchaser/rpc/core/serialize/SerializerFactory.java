package com.sunchaser.rpc.core.serialize;

import com.google.common.collect.Maps;
import com.sunchaser.rpc.core.serialize.impl.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.Optional;

/**
 * Serializer Factory（简单工厂模式）
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/12
 */
public class SerializerFactory {

    /**
     * 0000 0000 HESSIAN2
     * 0000 0001 JSON
     * 0000 0010 XML
     * 0000 0011 PROTOSTUFF
     * 0000 0100 KRYO
     * ......
     * <p>
     * 0000 1111 => 15 => 0xF
     * <p>
     * 0000 0000
     * 0000 0001
     * 0000 0010
     * 0000 0011
     * 0000 0100
     * ......
     */
    public static Serializer getSerializer(byte compressAndSerialize) {
        return SerializerEnum.match((byte) (compressAndSerialize & 0xF0)).getSerializer();
    }

    @Getter
    @AllArgsConstructor
    enum SerializerEnum {

        /**
         * hessian2
         */
        HESSIAN2((byte) 0x0, new Hessian2Serializer()),

        /**
         * json
         */
        JSON((byte) 0x1, new JsonSerializer()),

        /**
         * xml
         */
        XML((byte) 0x2, new XmlSerializer()),

        /**
         * protostuff
         */
        PROTOSTUFF((byte) 0x3, new ProtostuffSerializer()),

        /**
         * kryo
         */
        KRYO((byte) 0x4, new KryoSerializer()),

        ;

        private final byte val;

        private final Serializer serializer;

        private static final Map<Byte, SerializerEnum> ENUM_MAP = Maps.newHashMap();

        static {
            for (SerializerEnum serializerEnum : SerializerEnum.values()) {
                ENUM_MAP.put(serializerEnum.val, serializerEnum);
            }
        }

        public static SerializerEnum match(byte val) {
            return Optional.ofNullable(ENUM_MAP.get(val))
                    .orElse(HESSIAN2);
        }
    }
}
