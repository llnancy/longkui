package com.sunchaser.shushan.rpc.core.registry.impl;

import com.sunchaser.shushan.rpc.core.balancer.LoadBalancer;
import com.sunchaser.shushan.rpc.core.balancer.Node;
import com.sunchaser.shushan.rpc.core.balancer.impl.RoundRobinLoadBalancer;
import com.sunchaser.shushan.rpc.core.exceptions.RpcException;
import com.sunchaser.shushan.rpc.core.registry.Registry;
import com.sunchaser.shushan.rpc.core.registry.ServiceMetaData;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.x.discovery.ServiceCache;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 基于Zookeeper实现的服务注册与发现
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/14
 */
public class ZookeeperRegistry implements Registry {

    private static final Registry INSTANCE = new ZookeeperRegistry();

    public static Registry getInstance() {
        return INSTANCE;
    }

    private static final String DEFAULT_ZK_ADDRESS = "127.0.0.1:2181";

    private static final String ZK_BASE_PATH = "/sunchaser-rpc";

    private final CuratorFramework client;

    private final ServiceDiscovery<ServiceMetaData> serviceDiscovery;

    private final ServiceCache<ServiceMetaData> serviceCache;

    /**
     * 默认使用基于加权轮询算法的负载均衡器
     */
    private final LoadBalancer loadBalancer = new RoundRobinLoadBalancer();

    public ZookeeperRegistry() {
        this(DEFAULT_ZK_ADDRESS);
    }

    @SneakyThrows({Throwable.class, Exception.class})
    public ZookeeperRegistry(String zkAddress) {
        // 创建Curator客户端并启动
        client = CuratorFrameworkFactory.newClient(zkAddress, new ExponentialBackoffRetry(1000, 3));
        client.start();
        // 序列化器
        JsonInstanceSerializer<ServiceMetaData> serializer = new JsonInstanceSerializer<>(ServiceMetaData.class);
        // 创建ServiceDiscovery并启动
        this.serviceDiscovery = ServiceDiscoveryBuilder.builder(ServiceMetaData.class)
                .client(client)
                .serializer(serializer)
                .basePath(ZK_BASE_PATH)
                .watchInstances(true)
                .build();
        this.serviceDiscovery.start();
        // 创建ServiceCache并启动
        this.serviceCache = serviceDiscovery.serviceCacheBuilder()
                .name(ZK_BASE_PATH)
                .build();
        this.serviceCache.start();
    }

    /**
     * 服务注册
     *
     * @param serviceMetaData ServiceMetaData
     */
    @SneakyThrows({Throwable.class, Exception.class})
    @Override
    public void register(ServiceMetaData serviceMetaData) {
        ServiceInstance<ServiceMetaData> serviceInstance = ServiceInstance.<ServiceMetaData>builder()
                .name(serviceMetaData.getServiceName())
                .address(serviceMetaData.getHost())
                .port(serviceMetaData.getPort())
                .payload(serviceMetaData)
                .build();
        serviceDiscovery.registerService(serviceInstance);
    }

    /**
     * 服务注销
     *
     * @param serviceMetaData ServiceMetaData
     */
    @SneakyThrows({Throwable.class, Exception.class})
    @Override
    public void unRegister(ServiceMetaData serviceMetaData) {
        ServiceInstance<ServiceMetaData> serviceInstance = ServiceInstance.<ServiceMetaData>builder()
                .name(serviceMetaData.getServiceName())
                .address(serviceMetaData.getHost())
                .port(serviceMetaData.getPort())
                .payload(serviceMetaData)
                .build();
        serviceDiscovery.unregisterService(serviceInstance);
    }

    /**
     * 服务发现
     *
     * @param serviceName serviceName
     * @return ServiceMetaData
     */
    @SneakyThrows({Throwable.class, Exception.class})
    @Override
    public ServiceMetaData discovery(String serviceName, String methodName) {
        // 通过ServiceCache从本地缓存中获取服务实例
        List<ServiceInstance<ServiceMetaData>> serviceInstances = serviceCache.getInstances()
                .stream()
                .filter(el -> el.getName().equals(serviceName))
                .collect(Collectors.toList());
        // 缓存中不存在，则通过ServiceDiscovery直接从Zookeeper中获取服务实例
        if (CollectionUtils.isEmpty(serviceInstances)) {
            serviceInstances = (List<ServiceInstance<ServiceMetaData>>) serviceDiscovery.queryForInstances(serviceName);
        }
        if (CollectionUtils.isEmpty(serviceInstances)) {
            throw new RpcException("no service named " + serviceName + " was discovered");
        }
        // 获取第一个服务实例
        // return serviceInstances.get(0).getPayload();
        // 使用负载均衡器获取服务实例
        Node<ServiceInstance<ServiceMetaData>> select = loadBalancer.select(LoadBalancer.wrap(serviceInstances));
        if (Objects.isNull(select)) {
            throw new RpcException("no service named " + serviceName + " was discovered");
        }
        return select.getNode().getPayload();
    }

    /**
     * 注册中心销毁
     */
    @Override
    public void destroy() {
        CloseableUtils.closeQuietly(serviceDiscovery);
        CloseableUtils.closeQuietly(serviceCache);
        CloseableUtils.closeQuietly(client);
    }
}
