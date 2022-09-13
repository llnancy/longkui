package com.sunchaser.shushan.rpc.core.registry.impl;

import com.google.common.collect.Maps;
import com.sunchaser.shushan.rpc.core.registry.Registry;
import com.sunchaser.shushan.rpc.core.registry.ServiceMetaData;

import java.util.concurrent.ConcurrentMap;

/**
 * Local registry
 * Must be in a JVM process
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/17
 */
public class LocalRegistry implements Registry {

    private final ConcurrentMap<String, ServiceMetaData> services = Maps.newConcurrentMap();

    private LocalRegistry() {
    }

    private static final Registry INSTANCE = new LocalRegistry();

    public static Registry getInstance() {
        return INSTANCE;
    }

    @Override
    public void register(ServiceMetaData serviceMetaData) {
        services.put(serviceMetaData.getServiceKey(), serviceMetaData);
    }

    @Override
    public void unRegister(ServiceMetaData serviceMetaData) {
        services.remove(serviceMetaData.getServiceKey());
    }

    @Override
    public ServiceMetaData discovery(String serviceKey) {
        return services.get(serviceKey);
    }

    @Override
    public void destroy() {
        services.clear();
    }
}
