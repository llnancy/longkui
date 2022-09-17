package com.sunchaser.shushan.rpc.core.provider;

/**
 * service provider
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/18
 */
public interface ServiceProvider {

    /**
     * 注册服务提供者
     *
     * @param serviceKey service key
     * @param service    service object
     */
    void registerProvider(String serviceKey, Object service);

    /**
     * 获取服务提供者
     *
     * @param serviceKey service key
     * @return service object
     */
    Object getProvider(String serviceKey);
}
