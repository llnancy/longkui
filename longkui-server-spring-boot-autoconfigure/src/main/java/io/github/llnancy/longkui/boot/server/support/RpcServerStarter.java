/*
 * Copyright 2022 LongKui
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.llnancy.longkui.boot.server.support;

import com.google.common.base.Preconditions;
import io.github.llnancy.longkui.boot.server.annotation.RpcService;
import io.github.llnancy.longkui.core.common.Constants;
import io.github.llnancy.longkui.core.config.RpcServerConfig;
import io.github.llnancy.longkui.core.extension.ExtensionLoader;
import io.github.llnancy.longkui.core.provider.ServiceProvider;
import io.github.llnancy.longkui.core.provider.impl.InMemoryServiceProvider;
import io.github.llnancy.longkui.core.registry.Registry;
import io.github.llnancy.longkui.core.registry.ServiceMetaData;
import io.github.llnancy.longkui.core.transport.server.NettyRpcServer;
import io.github.llnancy.longkui.core.transport.server.RpcServer;
import io.github.llnancy.longkui.core.util.ServiceUtils;
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
