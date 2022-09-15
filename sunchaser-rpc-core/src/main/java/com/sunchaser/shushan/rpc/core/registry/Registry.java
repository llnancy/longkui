package com.sunchaser.shushan.rpc.core.registry;

import com.sunchaser.shushan.rpc.core.extension.SPI;

/**
 * 服务注册与发现接口
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/14
 */
@SPI
public interface Registry {

    /**
     * 服务注册
     *
     * @param serviceMetaData ServiceMetaData
     */
    void register(ServiceMetaData serviceMetaData);

    /**
     * 服务注销
     *
     * @param serviceMetaData ServiceMetaData
     */
    void unRegister(ServiceMetaData serviceMetaData);

    /**
     * 服务发现
     *
     * @param serviceKey serviceKey
     * @return ServiceMetaData
     */
    ServiceMetaData discovery(String serviceKey);

    /**
     * 注册中心销毁
     */
    void destroy();
}
