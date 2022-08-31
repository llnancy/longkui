package com.sunchaser.shushan.rpc.core.registry.impl;

import com.sunchaser.shushan.rpc.core.balancer.LoadBalancer;
import com.sunchaser.shushan.rpc.core.balancer.Node;
import com.sunchaser.shushan.rpc.core.exceptions.RpcException;
import com.sunchaser.shushan.rpc.core.registry.Registry;
import com.sunchaser.shushan.rpc.core.registry.ServiceMetaData;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Objects;

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
    public ServiceMetaData discovery(String serviceName, String methodName) {
        List<T> originalServiceList = doDiscoveryOriginalServiceList(serviceName, methodName);
        if (CollectionUtils.isNotEmpty(originalServiceList)) {
            throw new RpcException("no service named " + serviceName + " was discovered");
        }
        Node<T> select = loadBalancer.select(LoadBalancer.wrap(originalServiceList));
        if (Objects.isNull(select)) {
            throw new RpcException("no service named " + serviceName + " was discovered");
        }
        return doConvertToServiceMetaData(select);
    }

    /**
     * 从注册中心获取服务实例列表
     *
     * @param methodName  methodName
     * @param serviceName serviceName
     * @return 具体注册中心实现框架的服务实例对象列表
     */
    protected abstract List<T> doDiscoveryOriginalServiceList(String serviceName, String methodName);

    /**
     * 将负载均衡器选择出来的Node节点转化为ServiceMetaData
     *
     * @param select Node<T>
     * @return ServiceMetaData
     */
    protected abstract ServiceMetaData doConvertToServiceMetaData(Node<T> select);
}
