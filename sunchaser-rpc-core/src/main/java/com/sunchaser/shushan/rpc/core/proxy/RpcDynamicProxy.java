package com.sunchaser.shushan.rpc.core.proxy;

import com.sunchaser.shushan.rpc.core.config.RpcServiceConfig;
import com.sunchaser.shushan.rpc.core.extension.SPI;

/**
 * 接口：RpcDynamicProxy
 * 抽象实现类：AbstractRpcDynamicProxy
 * JDK动态代理实现类：JdkRpcDynamicProxy
 * Cglib动态代理实现类：CglibRpcDynamicProxy
 * Javassist动态代理实现类：JavassistRpcDynamicProxy
 * Byte Buddy动态代理实现类：ByteBuddyRpcDynamicProxy
 * <p>
 * rpc dynamic proxy interface
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/8/22
 */
@SPI
public interface RpcDynamicProxy {

    /**
     * 根据RpcServiceConfig创建并获取代理对象
     *
     * @param rpcServiceConfig rpc service config
     * @param <T>              代理对象的类型
     * @return 代理对象
     */
    <T> T createProxyInstance(RpcServiceConfig rpcServiceConfig);
}
