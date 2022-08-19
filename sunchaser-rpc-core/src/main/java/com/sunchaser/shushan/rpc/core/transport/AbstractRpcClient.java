package com.sunchaser.shushan.rpc.core.transport;

/**
 * an abstract rpc client implementation
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/15
 */
public abstract class AbstractRpcClient implements RpcClient {

    protected Integer connectionTimeout;

    public AbstractRpcClient(Integer connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }
}
