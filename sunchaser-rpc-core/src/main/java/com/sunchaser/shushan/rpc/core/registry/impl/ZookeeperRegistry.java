package com.sunchaser.shushan.rpc.core.registry.impl;

import com.sunchaser.shushan.rpc.core.balancer.Invoker;
import com.sunchaser.shushan.rpc.core.balancer.LoadBalancer;
import com.sunchaser.shushan.rpc.core.balancer.impl.RandomLoadBalancer;
import com.sunchaser.shushan.rpc.core.exceptions.RpcException;
import com.sunchaser.shushan.rpc.core.registry.Registry;
import com.sunchaser.shushan.rpc.core.registry.ServiceMeta;
import lombok.SneakyThrows;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;

import java.util.Collection;
import java.util.Objects;

/**
 * 基于Zookeeper实现的服务注册与发现
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/14
 */
public class ZookeeperRegistry implements Registry {

    private static final String DEFAULT_ZK_ADDRESS = "127.0.0.1:2181";

    private static final String ZK_BASE_PATH = "/sunchaser-rpc";

    private final ServiceDiscovery<ServiceMeta> serviceDiscovery;

    private final LoadBalancer loadBalancer;

    public ZookeeperRegistry() {
        this(DEFAULT_ZK_ADDRESS);
    }

    @SneakyThrows
    public ZookeeperRegistry(String zkAddress) {
        this.loadBalancer = new RandomLoadBalancer();
        CuratorFramework client = CuratorFrameworkFactory.newClient(zkAddress, new ExponentialBackoffRetry(1000, 3));
        client.start();
        JsonInstanceSerializer<ServiceMeta> serializer = new JsonInstanceSerializer<>(ServiceMeta.class);
        this.serviceDiscovery = ServiceDiscoveryBuilder.builder(ServiceMeta.class)
                .client(client)
                .serializer(serializer)
                .basePath(ZK_BASE_PATH)
                .build();
        this.serviceDiscovery.start();
    }

    /**
     * 服务注册
     *
     * @param service ServiceInstance
     */
    @SneakyThrows
    @Override
    public void register(ServiceMeta service) {
        ServiceInstance<ServiceMeta> serviceInstance = ServiceInstance.<ServiceMeta>builder()
                .name(service.getServiceName())
                .address(service.getAddress())
                .port(service.getPort())
                .payload(service)
                .build();
        serviceDiscovery.registerService(serviceInstance);
    }

    /**
     * 服务注销
     *
     * @param service ServiceInstance
     */
    @SneakyThrows
    @Override
    public void unRegister(ServiceMeta service) {
        ServiceInstance<ServiceMeta> serviceInstance = ServiceInstance.<ServiceMeta>builder()
                .name(service.getServiceName())
                .address(service.getAddress())
                .port(service.getPort())
                .payload(service)
                .build();
        serviceDiscovery.unregisterService(serviceInstance);
    }

    /**
     * 服务发现
     *
     * @param serviceName serviceName
     * @return ServiceMeta
     */
    @SneakyThrows
    @Override
    public ServiceMeta discovery(String serviceName, String methodName) {
        Collection<ServiceInstance<ServiceMeta>> serviceInstances = serviceDiscovery.queryForInstances(serviceName);
        Invoker<ServiceInstance<ServiceMeta>> select = loadBalancer.select(LoadBalancer.wrap(serviceInstances));
        if (Objects.isNull(select)) {
            throw new RpcException("no service named " + serviceName + " was discovered");
        }
        return select.getNode().getPayload();
    }

    /**
     * 注册中心销毁
     */
    @SneakyThrows
    @Override
    public void destroy() {
        serviceDiscovery.close();
    }
}
