package com.sunchaser.shushan.rpc.server.spring;

import com.sunchaser.shushan.rpc.core.common.Constants;
import com.sunchaser.shushan.rpc.core.extension.ExtensionLoader;
import com.sunchaser.shushan.rpc.core.provider.ServiceProvider;
import com.sunchaser.shushan.rpc.core.provider.impl.InMemoryServiceProvider;
import com.sunchaser.shushan.rpc.core.registry.Registry;
import com.sunchaser.shushan.rpc.core.registry.RegistryEnum;
import com.sunchaser.shushan.rpc.core.registry.ServiceMetaData;
import com.sunchaser.shushan.rpc.core.transport.server.RpcServer;
import com.sunchaser.shushan.rpc.server.annotation.RpcService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.lang.NonNull;

import java.net.InetAddress;

/**
 * rpc service bean post processor
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/21
 */
@Slf4j
public class RpcServiceBeanPostProcessor implements BeanPostProcessor {

    private final Registry registry;

    private final ServiceProvider serviceProvider;

    private final RpcServer rpcServer;

    public RpcServiceBeanPostProcessor() {
        this.registry = ExtensionLoader.getExtensionLoader(Registry.class).getExtension(RegistryEnum.ZOOKEEPER);
        this.serviceProvider = InMemoryServiceProvider.getInstance();
        this.rpcServer = ExtensionLoader.getExtensionLoader(RpcServer.class).getExtension(Constants.NETTY);
    }

    @SneakyThrows
    @Override
    public Object postProcessBeforeInitialization(Object bean, @NonNull String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        if (beanClass.isAnnotationPresent(RpcService.class)) {
            RpcService rpcService = beanClass.getAnnotation(RpcService.class);
            String serviceKey = String.join("#", bean.getClass().getName(), rpcService.version(), rpcService.group());
            serviceProvider.registerProvider(serviceKey, bean);
            int port = RpcServer.DEFAULT_PORT;
            ServiceMetaData serviceMetaData = ServiceMetaData.builder()
                    .serviceKey(serviceKey)
                    .host(InetAddress.getLocalHost().getHostAddress())
                    .port(port)
                    .weight(rpcService.weight())
                    .timestamp(System.currentTimeMillis())
                    .warmup(rpcService.warmup())
                    .build();
            registry.register(serviceMetaData);
            rpcServer.start();
            LOGGER.info("sunchaser-rpc >>>>>> server start on {}", port);
        }
        return bean;
    }
}
