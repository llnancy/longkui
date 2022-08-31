package com.sunchaser.shushan.rpc.core.registry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 服务元数据
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/14
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceMetaData {

    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 版本号
     */
    private String version;

    /**
     * 服务地址
     */
    private String host;

    /**
     * 端口号
     */
    private Integer port;
}
