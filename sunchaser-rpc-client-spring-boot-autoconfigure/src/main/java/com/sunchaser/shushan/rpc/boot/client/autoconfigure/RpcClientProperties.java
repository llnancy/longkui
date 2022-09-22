package com.sunchaser.shushan.rpc.boot.client.autoconfigure;

import com.sunchaser.shushan.rpc.boot.common.ThreadPoolProperties;
import com.sunchaser.shushan.rpc.core.config.RpcProtocolConfig;
import com.sunchaser.shushan.rpc.core.config.RpcServiceConfig;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * rpc client properties
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/21
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "sunchaser.rpc.client")
public class RpcClientProperties {

    /**
     * connection timeout
     */
    private Integer connectionTimeout;

    /**
     * io threads
     */
    private Integer ioThreads;

    /**
     * callback type thread pool config
     */
    @NestedConfigurationProperty
    private ThreadPoolProperties callbackThreadPool;

    /**
     * dynamic proxy
     */
    private String dynamicProxy;

    /**
     * registry
     */
    private String registry;

    /**
     * load balancer
     */
    private String loadBalancer;

    /**
     * rpc client (service consumer)
     */
    private String rpcClient;

    /**
     * rpc protocol config
     */
    @NestedConfigurationProperty
    private RpcProtocolConfig rpcProtocol;

    /**
     * rpc service config
     */
    @NestedConfigurationProperty
    private RpcServiceConfig rpcService;

}
