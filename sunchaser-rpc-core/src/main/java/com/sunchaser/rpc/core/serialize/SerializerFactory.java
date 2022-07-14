package com.sunchaser.rpc.core.serialize;

import com.google.common.collect.Maps;
import com.sunchaser.rpc.core.serialize.Serializer;
import com.sunchaser.rpc.core.serialize.impl.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.Optional;

/**
 * Serializer Factory
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/12
 */
public class SerializerFactory {

    /**
     * 0000 0000 HESSIAN2
     * 0001 0000 JSON
     * 0010 0000 XML
     * 0011 0000 PROTOSTUFF
     * 0100 0000 KRYO
     * <p>
     * 1111 0000 => 取反 0000 1111 => 加一 0001 0000 => -16 => 0xF0
     * <p>
     * 0000 0000
     * 0001 0000
     * 0010 0000
     * 0011 0000
     * 0100 0000
     */
    public static Serializer getSerializer(byte protocolInfo) {
        return SerializerEnum.match((byte) (protocolInfo & 0xF0)).getSerializer();
    }

    @Getter
    @AllArgsConstructor
    enum SerializerEnum {

        HESSIAN2((byte) 0x0, new Hessian2Serializer()),

        JSON((byte) 0x10, new JsonSerializer()),

        XML((byte) 0x20, new XmlSerializer()),

        PROTOSTUFF((byte) 0x30, new ProtostuffSerializer()),

        KRYO((byte) 0x40, new KryoSerializer()),
        ;

        private final byte val;

        private final Serializer serializer;

        private static final Map<Byte, SerializerEnum> enumMap = Maps.newHashMap();

        static {
            for (SerializerEnum serializerEnum : SerializerEnum.values()) {
                enumMap.put(serializerEnum.val, serializerEnum);
            }
        }

        public static SerializerEnum match(byte val) {
            return Optional.ofNullable(enumMap.get(val))
                    .orElse(HESSIAN2);
        }
    }
}
