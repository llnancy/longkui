package com.sunchaser.shushan.rpc.core.test;

import com.sunchaser.shushan.rpc.core.common.Constants;
import com.sunchaser.shushan.rpc.core.extension.ExtensionLoader;
import com.sunchaser.shushan.rpc.core.provider.ServiceProvider;
import com.sunchaser.shushan.rpc.core.provider.impl.InMemoryServiceProvider;
import com.sunchaser.shushan.rpc.core.transport.server.RpcServer;

/**
 * 点对点RPC服务提供者
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/8/18
 */
public class P2PProvider {

    public static void main(String[] args) throws Exception {
        ServiceProvider serviceProvider = InMemoryServiceProvider.getInstance();
        serviceProvider.registerProvider(HelloService.class.getName(), new HelloServiceImpl());
        RpcServer rpcServer = ExtensionLoader.getExtensionLoader(RpcServer.class).getExtension(Constants.NETTY);
        rpcServer.start();
    }
}
