package com.sunchaser.shushan.rpc.core.proxy;

import com.sunchaser.shushan.rpc.core.config.RpcFrameworkConfig;
import com.sunchaser.shushan.rpc.core.extension.SPI;

/**
 * 接口：DynamicProxy
 * 抽象实现类：AbstractDynamicProxy
 * JDK动态代理实现类：JdkDynamicProxy
 * Cglib动态代理实现类：CglibDynamicProxy
 * Javassist动态代理实现类：JavassistDynamicProxy
 * Byte Buddy动态代理实现类：ByteBuddyDynamicProxy
 * <p>
 * rpc dynamic proxy interface
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/8/22
 */
@SPI
public interface DynamicProxy {

    /**
     * 根据RpcServiceConfig创建并获取代理对象
     *
     * @param rpcFrameworkConfig rpc framework config
     * @param <T>                代理对象的类型
     * @return 代理对象
     */
    <T> T createProxyInstance(RpcFrameworkConfig rpcFrameworkConfig);
}
