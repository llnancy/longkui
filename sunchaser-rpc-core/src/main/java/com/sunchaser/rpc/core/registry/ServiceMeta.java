package com.sunchaser.rpc.core.registry;

import lombok.Data;

/**
 * 服务元数据
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/14
 */
@Data
public class ServiceMeta {

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
    private String address;

    /**
     * 端口号
     */
    private Integer port;
}
