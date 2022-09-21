package com.sunchaser.shushan.rpc.server.support;

import com.google.common.base.Preconditions;
import com.sunchaser.shushan.rpc.core.config.RpcServerConfig;
import com.sunchaser.shushan.rpc.core.extension.ExtensionLoader;
import com.sunchaser.shushan.rpc.core.provider.ServiceProvider;
import com.sunchaser.shushan.rpc.core.provider.impl.InMemoryServiceProvider;
import com.sunchaser.shushan.rpc.core.registry.Registry;
import com.sunchaser.shushan.rpc.core.registry.ServiceMetaData;
import com.sunchaser.shushan.rpc.core.transport.server.RpcServer;
import com.sunchaser.shushan.rpc.core.util.ServiceUtils;
import com.sunchaser.shushan.rpc.server.annotation.RpcService;
import com.sunchaser.shushan.rpc.server.autoconfigure.RpcServerProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.Objects;

/**
 * rpc service bean post processor
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/21
 */
@Slf4j
public class RpcServiceBeanPostProcessor implements ApplicationContextAware, InitializingBean, DisposableBean {

    private final RpcServerConfig rpcServerConfig;

    private final Registry registry;

    private final RpcServer rpcServer;

    private final ServiceProvider serviceProvider;

    public RpcServiceBeanPostProcessor(RpcServerProperties properties) {
        this.rpcServerConfig = properties.getConfig();
        this.registry = ExtensionLoader.getExtensionLoader(Registry.class).getExtension(rpcServerConfig.getRegistry());
        this.rpcServer = ExtensionLoader.getExtensionLoader(RpcServer.class).getExtension(rpcServerConfig.getRpcServer());
        this.serviceProvider = InMemoryServiceProvider.getInstance();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> beansMap = applicationContext.getBeansWithAnnotation(RpcService.class);
        if (CollectionUtils.isEmpty(beansMap)) {
            return;
        }
        for (Object bean : beansMap.values()) {
            Class<?> beanClass = bean.getClass();
            Preconditions.checkArgument(beanClass.getInterfaces().length != 0, "sunchaser-rpc >>>>>> rpc service must inherit interface.");
            RpcService rpcService = beanClass.getAnnotation(RpcService.class);
            if (Objects.isNull(rpcService)) {
                return;
            }
            String serviceKey = ServiceUtils.buildServiceKey(beanClass.getName(), rpcService.group(), rpcService.version());
            serviceProvider.registerProvider(serviceKey, bean);
            int port = rpcServerConfig.getPort();
            ServiceMetaData serviceMetaData = ServiceMetaData.builder()
                    .serviceKey(serviceKey)
                    .host(rpcServerConfig.getHost())
                    .port(port)
                    .weight(rpcService.weight())
                    .timestamp(System.currentTimeMillis())
                    .warmup(rpcService.warmup())
                    .build();
            registry.register(serviceMetaData);
            LOGGER.info("sunchaser-rpc >>>>>> server initialized with port(s): {} (tcp)", port);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        rpcServer.start();
    }

    @Override
    public void destroy() throws Exception {
        rpcServer.destroy();
    }
}
