package com.sunchaser.shushan.rpc.core.config;

import com.sunchaser.shushan.rpc.core.common.Constants;
import com.sunchaser.shushan.rpc.core.compress.CompressorEnum;
import com.sunchaser.shushan.rpc.core.serialize.SerializerEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * rpc protocol config
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class RpcProtocolConfig {

    private byte protocolVersion = Constants.DEFAULT_PROTOCOL_VERSION;

    private String compressor = CompressorEnum.SNAPPY.name().toLowerCase();

    private String serializer = SerializerEnum.HESSIAN2.name().toLowerCase();
}
