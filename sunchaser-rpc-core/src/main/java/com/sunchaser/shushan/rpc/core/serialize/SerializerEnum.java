package com.sunchaser.shushan.rpc.core.serialize;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Serializer Enum
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/15
 */
@AllArgsConstructor
@Getter
public enum SerializerEnum {

    /**
     * 序列化方式枚举
     */
    HESSIAN2, JSON, XML, PROTOSTUFF, KRYO
}
