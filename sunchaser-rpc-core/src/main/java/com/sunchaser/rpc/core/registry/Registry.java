package com.sunchaser.rpc.core.registry;

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
     * @param service ServiceInstance
     */
    void register(ServiceMeta service);

    /**
     * 服务注销
     *
     * @param service ServiceInstance
     */
    void unRegister(ServiceMeta service);

    /**
     * 服务发现
     *
     * @param serviceName serviceName
     * @return ServiceMeta
     */
    ServiceMeta discovery(String serviceName);

    /**
     * 注册中心销毁
     */
    void destroy();
}
