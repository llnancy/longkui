package com.sunchaser.shushan.rpc.core.test;

import com.sunchaser.shushan.rpc.core.transport.server.NettyRpcServer;
import com.sunchaser.shushan.rpc.core.util.BeanFactory;

/**
 * 点对点RPC服务提供者
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/8/18
 */
public class P2PProvider {

    public static void main(String[] args) throws Exception {
        // 不指定threadFactoryName的NioEventLoopGroup不会导致main方法退出
        // NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        // NioEventLoopGroup workerGroup = new NioEventLoopGroup(4);
        BeanFactory.register(HelloService.class.getName(), new HelloServiceImpl());
        new NettyRpcServer().start(1234);
        System.in.read();
    }
}
