/*
 * Copyright 2022 SunChaser
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

package com.sunchaser.shushan.rpc.boot.client.support;

import com.sunchaser.shushan.rpc.boot.client.annotation.RpcReference;
import com.sunchaser.shushan.rpc.core.config.RpcClientConfig;
import com.sunchaser.shushan.rpc.core.config.RpcServiceConfig;
import com.sunchaser.shushan.rpc.core.extension.ExtensionLoader;
import com.sunchaser.shushan.rpc.core.proxy.DynamicProxy;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.NonNull;
import org.springframework.util.ReflectionUtils;

import java.util.Objects;

/**
 * rpc reference bean post processor
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/21
 */
public class RpcReferenceBeanPostProcessor implements BeanPostProcessor {

    private final RpcClientConfig rpcClientConfig;

    private final DynamicProxy dynamicProxy;

    public RpcReferenceBeanPostProcessor(RpcClientConfig config) {
        this.rpcClientConfig = config;
        this.dynamicProxy = ExtensionLoader.getExtensionLoader(DynamicProxy.class).getExtension(rpcClientConfig.getDynamicProxy());
    }

    /**
     * bean初始化后置处理方法
     *
     * @param bean     IOC容器初始化（调过init-method）后的bean对象
     * @param beanName bean name
     * @return 增强后的bean
     * @throws BeansException throws
     */
    @Override
    public Object postProcessAfterInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        // AOP工具类获取bean对应的Class
        Class<?> clazz = AopProxyUtils.ultimateTargetClass(bean);
        // doWithFields方法，对给定的clazz类上的所有字段执行给定的回调
        ReflectionUtils.doWithFields(clazz, field -> {
            // 获取被@RpcReference注解标记的字段
            RpcReference rpcReference = AnnotationUtils.getAnnotation(field, RpcReference.class);
            if (Objects.nonNull(rpcReference)) {
                field.setAccessible(true);
                RpcServiceConfig rpcServiceConfig = createRpcServiceConfig(field.getType(), rpcReference);
                // 动态将字段的值修改为代理对象
                ReflectionUtils.setField(field, bean, dynamicProxy.createProxyInstance(rpcClientConfig, rpcServiceConfig));
            }
        });
        return bean;
    }

    private static <T> RpcServiceConfig createRpcServiceConfig(Class<T> clazz, RpcReference rpcReference) {
        RpcServiceConfig rpcServiceConfig = RpcServiceConfig.createDefaultConfig(clazz);
        rpcServiceConfig.setVersion(rpcReference.version())
                .setGroup(rpcReference.group())
                .setTimeout(rpcReference.timeout())
                .setCallType(rpcReference.callType());
        return rpcServiceConfig;
    }
}
