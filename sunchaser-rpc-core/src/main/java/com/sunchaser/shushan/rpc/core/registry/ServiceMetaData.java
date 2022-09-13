package com.sunchaser.shushan.rpc.core.registry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 注册中心服务元数据
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
     * 服务名称（包含接口名+版本号version+分组group）
     */
    private String serviceKey;

    /**
     * 服务地址
     */
    private String host;

    /**
     * 端口号
     */
    private Integer port;

    /**
     * 权重
     */
    private int weight;

    /**
     * 服务启动时间戳
     */
    private long timestamp;

    /**
     * 服务预热时间
     */
    private int warmup;
}
