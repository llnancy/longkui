package com.sunchaser.shushan.rpc.core.config;

import com.sunchaser.shushan.rpc.core.balancer.LoadBalancerEnum;
import com.sunchaser.shushan.rpc.core.common.Constants;
import com.sunchaser.shushan.rpc.core.proxy.DynamicProxyEnum;
import lombok.*;

/**
 * rpc client config
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class RpcClientConfig {

    /**
     * default dynamic proxy implementation. JDK
     */
    private static final String DEFAULT_DYNAMIC_PROXY = DynamicProxyEnum.JDK.name().toLowerCase();

    /**
     * default load balancer. round robin
     */
    private static final String DEFAULT_LOAD_BALANCER = LoadBalancerEnum.ROUND_ROBIN.name().replaceAll(Constants.UNDERLINE, Constants.EMPTY).toLowerCase();

    /**
     * connection timeout
     */
    private Integer connectionTimeout = Constants.DEFAULT_CONNECTION_TIMEOUT;

    /**
     * io threads
     */
    private Integer ioThreads = Constants.DEFAULT_IO_THREADS;

    /**
     * callback type thread pool config
     */
    private ThreadPoolConfig callbackThreadPoolConfig = ThreadPoolConfig.createDefaultConfig();

    /**
     * dynamic proxy
     */
    private String dynamicProxy = DEFAULT_DYNAMIC_PROXY;

    /**
     * registry
     */
    private String registry = Constants.DEFAULT_REGISTRY;

    /**
     * load balancer
     */
    private String loadBalancer = DEFAULT_LOAD_BALANCER;

    /**
     * rpc client (service consumer)
     */
    private String rpcClient = Constants.NETTY;

    /**
     * rpc protocol config
     */
    private RpcProtocolConfig rpcProtocolConfig = RpcProtocolConfig.createDefaultConfig();

    /**
     * rpc service config
     */
    private RpcServiceConfig rpcServiceConfig;

    public static RpcClientConfig createDefaultConfig() {
        return new RpcClientConfig();
    }

    public static <T> RpcClientConfig createDefaultConfig(Class<T> clazz) {
        RpcServiceConfig rpcServiceConfig = RpcServiceConfig.createDefaultConfig(clazz);
        return createDefaultConfig().setRpcServiceConfig(rpcServiceConfig);
    }
}
