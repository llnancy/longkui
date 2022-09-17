package com.sunchaser.shushan.rpc.core.config;

import com.sunchaser.shushan.rpc.core.balancer.LoadBalancerEnum;
import com.sunchaser.shushan.rpc.core.common.Constants;
import com.sunchaser.shushan.rpc.core.proxy.DynamicProxyEnum;
import com.sunchaser.shushan.rpc.core.registry.RegistryEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * rpc component config
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RpcComponentConfig {

    /**
     * 默认动态代理实现 JDK
     */
    private static final String DEFAULT_DYNAMIC_PROXY = DynamicProxyEnum.JDK.name().toLowerCase();

    /**
     * 默认注册中心实现 zookeeper
     */
    private static final String DEFAULT_REGISTRY = RegistryEnum.ZOOKEEPER.name().toLowerCase();

    /**
     * 默认负载均衡算法 轮询
     */
    private static final String DEFAULT_LOAD_BALANCER = LoadBalancerEnum.ROUND_ROBIN.name().replaceAll(Constants.UNDERLINE, Constants.EMPTY).toLowerCase();

    /**
     * 动态代理
     */
    private String dynamicProxy = DEFAULT_DYNAMIC_PROXY;

    /**
     * 注册中心
     */
    private String registry = DEFAULT_REGISTRY;

    /**
     * 负载均衡
     */
    private String loadBalancer = DEFAULT_LOAD_BALANCER;

    /**
     * rpc客户端（服务消费者）
     */
    private String rpcClient = Constants.NETTY;

    /**
     * rpc服务端（服务提供者）
     */
    private String rpcServer = Constants.NETTY;

    public static RpcComponentConfig createDefaultConfig() {
        return new RpcComponentConfig();
    }
}
