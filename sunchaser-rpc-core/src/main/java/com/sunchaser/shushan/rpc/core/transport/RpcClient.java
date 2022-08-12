package com.sunchaser.shushan.rpc.core.transport;

import com.sunchaser.shushan.rpc.core.protocol.RpcProtocol;

/**
 * rpc client interface
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/15
 */
public interface RpcClient<T> {

    /**
     * invoke
     *
     * @param rpcProtocol protocol
     */
    void invoke(RpcProtocol<T> rpcProtocol);
}
