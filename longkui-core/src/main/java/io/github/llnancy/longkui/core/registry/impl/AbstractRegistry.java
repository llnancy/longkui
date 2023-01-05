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
import io.github.llnancy.longkui.core.balancer.LoadBalancer;
import io.github.llnancy.longkui.core.balancer.Node;
import io.github.llnancy.longkui.core.registry.Registry;
import io.github.llnancy.longkui.core.registry.ServiceMetaData;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * 抽象注册中心
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/8/30
 */
public abstract class AbstractRegistry<T> implements Registry {

    private final LoadBalancer loadBalancer;

    public AbstractRegistry(LoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    @Override
    public ServiceMetaData discovery(String serviceKey) {
        List<T> registryServices = doDiscoveryFromRegistry(serviceKey);
        Preconditions.checkArgument(CollectionUtils.isNotEmpty(registryServices), "no service instance named " + serviceKey + " was discovered.");
        Node<T> select = loadBalancer.select(LoadBalancer.wrap(registryServices));
        Preconditions.checkNotNull(select, "no service instance named " + serviceKey + " be selected by loadbalancer.");
        return doConvertToServiceMetaData(select);
    }

    /**
     * 从注册中心获取服务实例列表
     *
     * @param serviceKey serviceKey
     * @return 具体注册中心实现框架的服务实例对象列表
     */
    protected abstract List<T> doDiscoveryFromRegistry(String serviceKey);

    /**
     * 将负载均衡器选择出来的Node节点转化为ServiceMetaData
     *
     * @param select Node<T>
     * @return ServiceMetaData
     */
    protected abstract ServiceMetaData doConvertToServiceMetaData(Node<T> select);
}
