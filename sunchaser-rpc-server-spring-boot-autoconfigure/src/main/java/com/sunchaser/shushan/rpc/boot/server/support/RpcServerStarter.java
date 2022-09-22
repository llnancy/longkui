package com.sunchaser.shushan.rpc.boot.server.support;

import com.google.common.base.Preconditions;
import com.sunchaser.shushan.rpc.boot.server.annotation.RpcService;
import com.sunchaser.shushan.rpc.core.common.Constants;
import com.sunchaser.shushan.rpc.core.config.RpcServerConfig;
import com.sunchaser.shushan.rpc.core.extension.ExtensionLoader;
import com.sunchaser.shushan.rpc.core.provider.ServiceProvider;
import com.sunchaser.shushan.rpc.core.provider.impl.InMemoryServiceProvider;
import com.sunchaser.shushan.rpc.core.registry.Registry;
import com.sunchaser.shushan.rpc.core.registry.ServiceMetaData;
import com.sunchaser.shushan.rpc.core.transport.server.NettyRpcServer;
import com.sunchaser.shushan.rpc.core.transport.server.RpcServer;
import com.sunchaser.shushan.rpc.core.util.ServiceUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.CollectionUtils;

import java.net.InetAddress;
import java.util.Map;
import java.util.Objects;

/**
 * rpc server starter
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/21
 */
@Slf4j
public class RpcServerStarter implements ApplicationContextAware, InitializingBean, DisposableBean {

    private final Integer port;

    private final Registry registry;

    private final RpcServer rpcServer;

    private final ServiceProvider serviceProvider;

    public RpcServerStarter(RpcServerConfig config) {
        this.port = config.getPort();
        this.registry = ExtensionLoader.getExtensionLoader(Registry.class).getExtension(config.getRegistry());
        String rpcServer = config.getRpcServer();
        if (Constants.NETTY.equals(rpcServer)) {
            this.rpcServer = new NettyRpcServer(config);
        } else {
            this.rpcServer = ExtensionLoader.getExtensionLoader(RpcServer.class).getExtension(rpcServer);
        }
        this.serviceProvider = InMemoryServiceProvider.getInstance();
    }

    @SneakyThrows
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> beansMap = applicationContext.getBeansWithAnnotation(RpcService.class);
        if (CollectionUtils.isEmpty(beansMap)) {
            return;
        }
        for (Object bean : beansMap.values()) {
            Class<?> beanClass = bean.getClass();
            Class<?>[] interfaces = beanClass.getInterfaces();
            Preconditions.checkArgument(interfaces.length != 0, "sunchaser-rpc >>>>>> rpc service must inherit interface.");
            RpcService rpcService = beanClass.getAnnotation(RpcService.class);
            if (Objects.isNull(rpcService)) {
                return;
            }
            String serviceKey = ServiceUtils.buildServiceKey(interfaces[0].getCanonicalName(), rpcService.group(), rpcService.version());
            serviceProvider.registerProvider(serviceKey, bean);
            ServiceMetaData serviceMetaData = ServiceMetaData.builder()
                    .serviceKey(serviceKey)
                    .host(InetAddress.getLocalHost().getHostAddress())
                    .port(port)
                    .weight(rpcService.weight())
                    .timestamp(System.currentTimeMillis())
                    .warmup(rpcService.warmup())
                    .build();
            registry.register(serviceMetaData);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        rpcServer.start(port);
    }

    @Override
    public void destroy() throws Exception {
        rpcServer.destroy();
    }
}
