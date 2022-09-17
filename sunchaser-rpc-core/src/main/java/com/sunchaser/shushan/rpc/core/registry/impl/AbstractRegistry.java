package com.sunchaser.shushan.rpc.core.registry.impl;

import com.google.common.base.Preconditions;
import com.sunchaser.shushan.rpc.core.balancer.LoadBalancer;
import com.sunchaser.shushan.rpc.core.balancer.Node;
import com.sunchaser.shushan.rpc.core.registry.Registry;
import com.sunchaser.shushan.rpc.core.registry.ServiceMetaData;
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
