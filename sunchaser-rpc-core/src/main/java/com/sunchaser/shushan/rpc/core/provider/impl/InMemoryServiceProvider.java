package com.sunchaser.shushan.rpc.core.provider.impl;

import com.google.common.collect.Maps;
import com.sunchaser.shushan.rpc.core.exceptions.RpcException;
import com.sunchaser.shushan.rpc.core.provider.ServiceProvider;
import lombok.SneakyThrows;

import java.util.Map;
import java.util.Objects;

/**
 * a service provider implementation based on memory
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/18
 */
public class InMemoryServiceProvider implements ServiceProvider {

    private static final Map<String, Object> SERVICE_PROVIDER_MAP = Maps.newConcurrentMap();

    private static final ServiceProvider INSTANCE = new InMemoryServiceProvider();

    public static ServiceProvider getInstance() {
        return INSTANCE;
    }

    /**
     * 注册服务提供者实例
     *
     * @param serviceKey service key
     * @param service    service object
     */
    @SneakyThrows
    @Override
    public void registerProvider(String serviceKey, Object service) {
        SERVICE_PROVIDER_MAP.put(serviceKey, service);
    }

    /**
     * 获取服务提供者实例
     *
     * @param serviceKey service key
     * @return service object
     * @throws RpcException throws on service be null
     */
    @Override
    public Object getProvider(String serviceKey) {
        Object service = SERVICE_PROVIDER_MAP.get(serviceKey);
        if (Objects.isNull(service)) {
            throw new RpcException(serviceKey + ". service does not exist.");
        }
        return service;
    }
}
