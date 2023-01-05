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

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.BeanCopier;
import cn.hutool.core.bean.copier.CopyOptions;
import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.CacheRefreshedEvent;
import com.netflix.discovery.DefaultEurekaClientConfig;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import com.netflix.discovery.shared.Applications;
import io.github.llnancy.longkui.core.balancer.LoadBalancer;
import io.github.llnancy.longkui.core.balancer.Node;
import io.github.llnancy.longkui.core.balancer.impl.RandomLoadBalancer;
import io.github.llnancy.longkui.core.exceptions.RpcException;
import io.github.llnancy.longkui.core.registry.Registry;
import io.github.llnancy.longkui.core.registry.ServiceMetaData;
import org.apache.commons.collections4.CollectionUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 基于Eureka实现的服务注册与发现
 * Borrowed from org.apache.dubbo.registry.eureka.EurekaServiceDiscovery
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/15
 */
public class EurekaRegistry implements Registry {

    private EurekaClient eurekaClient;

    private ApplicationInfoManager applicationInfoManager;

    /**
     * last apps hash code is used to identify the {@link Applications} is changed or not
     */
    private String lastAppsHashCode;

    private final LoadBalancer loadBalancer;

    public EurekaRegistry() {
        this.loadBalancer = new RandomLoadBalancer();
    }

    @Override
    public void register(ServiceMetaData serviceMetaData) {
        initEurekaClient(serviceMetaData);
        setInstanceStatus(InstanceInfo.InstanceStatus.UP);
    }

    /**
     * init eureka client
     *
     * @param serviceMetaData ServiceMetaData
     */
    private void initEurekaClient(ServiceMetaData serviceMetaData) {
        if (Objects.nonNull(this.eurekaClient)) {
            return;
        }
        initApplicationInfoManager(serviceMetaData);
        EurekaClient eurekaClient = createEurekaClient();
        registerEurekaEventListener(eurekaClient);
        // set eurekaClient
        this.eurekaClient = eurekaClient;
    }

    private void registerEurekaEventListener(EurekaClient eurekaClient) {
        eurekaClient.registerEventListener(event -> {
            if (event instanceof CacheRefreshedEvent) {
                onCacheRefreshedEvent((CacheRefreshedEvent) event);
            }
        });
    }

    /**
     * on CacheRefreshedEvent
     *
     * @param event CacheRefreshedEvent
     */
    private void onCacheRefreshedEvent(CacheRefreshedEvent event) {
        // Make sure thread-safe in async execution
        synchronized (this) {
            Applications applications = eurekaClient.getApplications();
            String appsHashCode = applications.getAppsHashCode();
            // changed
            if (!Objects.equals(lastAppsHashCode, appsHashCode)) {
                // todo update local cache
                // update current result
                lastAppsHashCode = appsHashCode;
            }
        }
    }

    private EurekaClient createEurekaClient() {
        DefaultEurekaClientConfig eurekaClientConfig = new DefaultEurekaClientConfig();
        return new DiscoveryClient(this.applicationInfoManager, eurekaClientConfig);
    }

    /**
     * init ApplicationInfoManager
     *
     * @param serviceMetaData ServiceMetaData
     */
    private void initApplicationInfoManager(ServiceMetaData serviceMetaData) {
        ConfigurableEurekaInstanceConfig eurekaInstanceConfig = new ConfigurableEurekaInstanceConfig()
                .setAppname(serviceMetaData.getServiceKey())
                .setIpAddress(serviceMetaData.getHost())
                .setNonSecurePort(serviceMetaData.getPort())
                .setMetadataMap(beanToMap(serviceMetaData));
        this.applicationInfoManager = new ApplicationInfoManager(eurekaInstanceConfig, (ApplicationInfoManager.OptionalArgs) null);
    }

    private Map<String, String> beanToMap(ServiceMetaData serviceMetaData) {
        return BeanCopier.create(serviceMetaData, new LinkedHashMap<String, String>(16, 1),
                CopyOptions.create()
                        .setIgnoreNullValue(false)
        ).copy();
    }

    @Override
    public void unRegister(ServiceMetaData serviceMetaData) {
        setInstanceStatus(InstanceInfo.InstanceStatus.OUT_OF_SERVICE);
    }

    @Override
    public ServiceMetaData discovery(String serviceKey) {
        Application application = this.eurekaClient.getApplication(serviceKey);
        if (Objects.isNull(application)) {
            return null;
        }
        List<InstanceInfo> infos = application.getInstances();
        if (CollectionUtils.isNotEmpty(infos)) {
            throw new RpcException("no service named " + serviceKey + " was discovered");
        }
        Node<InstanceInfo> select = loadBalancer.select(LoadBalancer.wrap(infos));
        if (Objects.isNull(select)) {
            throw new RpcException("no service named " + serviceKey + " was discovered");
        }
        return BeanUtil.mapToBean(select.getNode().getMetadata(), ServiceMetaData.class, false, null);
    }

    @Override
    public void destroy() {
        if (Objects.nonNull(this.eurekaClient)) {
            this.eurekaClient.shutdown();
        }
    }

    private void setInstanceStatus(InstanceInfo.InstanceStatus status) {
        if (Objects.nonNull(this.applicationInfoManager)) {
            this.applicationInfoManager.setInstanceStatus(status);
        }
    }
}
