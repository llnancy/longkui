package com.sunchaser.shushan.rpc.core.registry.impl;

import com.google.common.collect.Maps;
import com.sunchaser.shushan.rpc.core.registry.Registry;
import com.sunchaser.shushan.rpc.core.registry.ServiceMeta;

import java.util.concurrent.ConcurrentMap;

/**
 * Local registry
 * Must be in a JVM process
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/17
 */
public class LocalRegistry implements Registry {

    private final ConcurrentMap<String, ServiceMeta> services = Maps.newConcurrentMap();

    private LocalRegistry() {
    }

    private static final Registry INSTANCE = new LocalRegistry();

    public static Registry getInstance() {
        return INSTANCE;
    }

    @Override
    public void register(ServiceMeta service) {
        services.put(service.getServiceName(), service);
    }

    @Override
    public void unRegister(ServiceMeta service) {
        services.remove(service.getServiceName());
    }

    @Override
    public ServiceMeta discovery(String serviceName, String methodName) {
        return services.get(serviceName);
    }

    @Override
    public void destroy() {
        services.clear();
    }
}
