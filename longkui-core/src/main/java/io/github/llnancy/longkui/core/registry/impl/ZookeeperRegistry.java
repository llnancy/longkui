/*
 * Copyright 2023 LongKui
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Copyright 2022 LongKui
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.llnancy.longkui.core.registry.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import io.github.llnancy.longkui.core.balancer.LoadBalancer;
import io.github.llnancy.longkui.core.balancer.Node;
import io.github.llnancy.longkui.core.balancer.impl.RoundRobinLoadBalancer;
import io.github.llnancy.longkui.core.registry.Registry;
import io.github.llnancy.longkui.core.registry.ServiceMetaData;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.x.discovery.ServiceCache;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.apache.curator.x.discovery.details.ServiceCacheListener;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * 基于Zookeeper实现的服务注册与发现
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/14
 */
@Slf4j
public class ZookeeperRegistry implements Registry {

    private static final Registry INSTANCE = new ZookeeperRegistry();

    public static Registry getInstance() {
        return INSTANCE;
    }

    private static final String DEFAULT_ZK_ADDRESS = "127.0.0.1:2181";

    private static final int DEFAULT_BASE_SLEEP_TIME_MS = 1000;

    private static final int DEFAULT_MAX_RETRIES = 3;

    private static final String ZK_BASE_PATH = "/longkui-rpc";

    private final CuratorFramework client;

    private final ServiceDiscovery<ServiceMetaData> serviceDiscovery;

    private final ConcurrentMap<String, ServiceCache<ServiceMetaData>> serviceCacheMap;

    /**
     * 默认使用基于加权轮询算法的负载均衡器
     */
    private final LoadBalancer loadBalancer = new RoundRobinLoadBalancer();

    public ZookeeperRegistry() {
        this(DEFAULT_ZK_ADDRESS);
    }

    @SneakyThrows
    public ZookeeperRegistry(String zkAddress) {
        // 创建Curator客户端并启动
        client = CuratorFrameworkFactory.newClient(zkAddress, new ExponentialBackoffRetry(DEFAULT_BASE_SLEEP_TIME_MS, DEFAULT_MAX_RETRIES));
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
        this.serviceCacheMap = Maps.newConcurrentMap();
    }

    /**
     * 服务注册
     *
     * @param serviceMetaData ServiceMetaData
     */
    @SneakyThrows
    @Override
    public void register(ServiceMetaData serviceMetaData) {
        ServiceInstance<ServiceMetaData> serviceInstance = ServiceInstance.<ServiceMetaData>builder()
                .name(serviceMetaData.getServiceKey())
                .address(serviceMetaData.getHost())
                .port(serviceMetaData.getPort())
                .registrationTimeUTC(serviceMetaData.getTimestamp())
                .payload(serviceMetaData)
                .build();
        serviceDiscovery.registerService(serviceInstance);
    }

    /**
     * 服务注销
     *
     * @param serviceMetaData ServiceMetaData
     */
    @SneakyThrows
    @Override
    public void unRegister(ServiceMetaData serviceMetaData) {
        ServiceInstance<ServiceMetaData> serviceInstance = ServiceInstance.<ServiceMetaData>builder()
                .name(serviceMetaData.getServiceKey())
                .address(serviceMetaData.getHost())
                .port(serviceMetaData.getPort())
                .registrationTimeUTC(serviceMetaData.getTimestamp())
                .payload(serviceMetaData)
                .build();
        serviceDiscovery.unregisterService(serviceInstance);
    }

    /**
     * 服务发现
     *
     * @param serviceKey serviceKey
     * @return ServiceMetaData
     */
    @SneakyThrows
    @Override
    public ServiceMetaData discovery(String serviceKey) {
        @SuppressWarnings("all")
        ServiceCache<ServiceMetaData> serviceCache = this.serviceCacheMap.computeIfAbsent(serviceKey, v -> buildAndStartServiceCache(serviceKey));
        // 通过ServiceCache从本地缓存中获取服务实例
        List<ServiceInstance<ServiceMetaData>> serviceInstances = serviceCache.getInstances();
        // 缓存中不存在，则通过ServiceDiscovery直接从Zookeeper中获取服务实例
        if (CollectionUtils.isEmpty(serviceInstances)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("serviceCache don't have service instance named {}, will query from zookeeper", serviceKey);
            }
            serviceInstances = (List<ServiceInstance<ServiceMetaData>>) serviceDiscovery.queryForInstances(serviceKey);
        }
        Preconditions.checkArgument(CollectionUtils.isNotEmpty(serviceInstances), "no service instance named " + serviceKey + " was discovered.");
        // 获取第一个服务实例
        // return serviceInstances.get(0).getPayload();
        List<ImmutablePair<Integer, Integer>> weightPairList = serviceInstances.stream()
                .map(ins -> {
                    ServiceMetaData payload = ins.getPayload();
                    return ImmutablePair.of(payload.getWeight(), payload.getWarmup());
                })
                .collect(Collectors.toList());
        // 使用负载均衡器获取服务实例
        Node<ServiceInstance<ServiceMetaData>> select = loadBalancer.select(LoadBalancer.wrap(serviceInstances, weightPairList));
        Preconditions.checkNotNull(select, "no service instance named " + serviceKey + " be selected by loadbalancer.");
        return select.getNode().getPayload();
    }

    /**
     * build and start service cache
     *
     * @param serviceKey service key
     * @return ServiceCache
     */
    @SneakyThrows
    private ServiceCache<ServiceMetaData> buildAndStartServiceCache(String serviceKey) {
        ServiceCache<ServiceMetaData> cache = this.serviceDiscovery.serviceCacheBuilder()
                .name(serviceKey)
                .build();
        cache.addListener(new ServiceCacheListener() {

            @Override
            public void cacheChanged() {
                LOGGER.info("service instance {} modified, cacheChanged", serviceKey);
            }

            @Override
            public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState) {
                LOGGER.info("service instance {} modified, stateChanged, connectionState={}", serviceKey, connectionState);
            }
        });
        cache.start();
        return cache;
    }

    /**
     * 注册中心销毁
     */
    @Override
    public void destroy() {
        CloseableUtils.closeQuietly(serviceDiscovery);
        CloseableUtils.closeQuietly(client);
        for (Map.Entry<String, ServiceCache<ServiceMetaData>> entry : serviceCacheMap.entrySet()) {
            ServiceCache<ServiceMetaData> serviceCache = entry.getValue();
            CloseableUtils.closeQuietly(serviceCache);
        }
    }
}
