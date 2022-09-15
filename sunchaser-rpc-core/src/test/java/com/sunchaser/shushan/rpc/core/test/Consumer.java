package com.sunchaser.shushan.rpc.core.test;

import com.sunchaser.shushan.rpc.core.config.RpcFrameworkConfig;
import com.sunchaser.shushan.rpc.core.config.RpcServiceConfig;
import com.sunchaser.shushan.rpc.core.extension.ExtensionLoader;
import com.sunchaser.shushan.rpc.core.proxy.DynamicProxy;
import com.sunchaser.shushan.rpc.core.proxy.DynamicProxyEnum;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;

/**
 * rpc consumer
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/17
 */
@Slf4j
public class Consumer {

    public static void main(String[] args) throws Exception {
        RpcFrameworkConfig rpcFrameworkConfig = RpcFrameworkConfig.createDefaultConfig(HelloService.class);
        // RpcDynamicProxy rpcDynamicProxy = JdkRpcDynamicProxy.getInstance();
        DynamicProxy dynamicProxy = ExtensionLoader.getExtensionLoader(DynamicProxy.class).getExtension(DynamicProxyEnum.JDK);
        HelloService helloService = dynamicProxy.createProxyInstance(rpcFrameworkConfig);
        String hello = helloService.sayHello("SunChaser");
        LOGGER.info("sayHello result: {}", hello);
        Assertions.assertEquals("Hello:" + "SunChaser", hello);
        System.in.read();
    }
}
