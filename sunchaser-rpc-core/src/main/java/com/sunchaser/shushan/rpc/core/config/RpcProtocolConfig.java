package com.sunchaser.shushan.rpc.core.config;

import com.sunchaser.shushan.rpc.core.common.Constants;
import com.sunchaser.shushan.rpc.core.compress.CompressorEnum;
import com.sunchaser.shushan.rpc.core.serialize.SerializerEnum;
import com.sunchaser.shushan.rpc.core.uid.SequenceIdGeneratorEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * rpc protocol config
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RpcProtocolConfig {

    /**
     * 默认序列化算法 hessian2
     */
    private static final String DEFAULT_SERIALIZER = SerializerEnum.HESSIAN2.name().toLowerCase();

    /**
     * 默认压缩算法 snappy
     */
    private static final String DEFAULT_COMPRESSOR = CompressorEnum.SNAPPY.name().toLowerCase();

    /**
     * 默认序列ID生成器
     */
    private static final String DEFAULT_SEQUENCE_ID_GENERATOR = SequenceIdGeneratorEnum.ATOMIC_LONG.name().replaceAll(Constants.UNDERLINE, Constants.EMPTY).toLowerCase();

    /**
     * version
     */
    private byte protocolVersion = Constants.DEFAULT_PROTOCOL_VERSION;

    /**
     * 序列化算法
     */
    private String serializer = DEFAULT_SERIALIZER;

    /**
     * 压缩算法
     */
    private String compressor = DEFAULT_COMPRESSOR;

    /**
     * 序列ID生成器
     */
    private String sequenceIdGenerator = DEFAULT_SEQUENCE_ID_GENERATOR;

    public static RpcProtocolConfig createDefaultConfig() {
        return new RpcProtocolConfig();
    }
}
