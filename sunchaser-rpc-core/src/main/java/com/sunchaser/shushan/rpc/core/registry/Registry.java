package com.sunchaser.shushan.rpc.core.registry;

/**
 * 服务注册与发现接口
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/14
 */
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
     * @param serviceName serviceName
     * @param methodName  methodName
     * @return ServiceMetaData
     */
    ServiceMetaData discovery(String serviceName, String methodName);

    /**
     * 注册中心销毁
     */
    void destroy();
}
