/*
 * Copyright 2022 LongKui
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.llnancy.longkui.boot.client.autoconfigure;

import io.github.llnancy.longkui.boot.client.support.RpcReferenceBeanPostProcessor;
import io.github.llnancy.longkui.core.config.RpcClientConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * rpc client auto configuration
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/21
 */
@Configuration
@EnableConfigurationProperties({RpcClientProperties.class})
public class RpcClientAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(RpcReferenceBeanPostProcessor.class)
    public RpcReferenceBeanPostProcessor rpcReferenceBeanPostProcessor(RpcClientProperties properties) {
        RpcClientConfig rpcClientConfig = RpcClientConfig.createDefaultConfig();
        new RpcClientConfigurer(properties).configure(rpcClientConfig);
        return new RpcReferenceBeanPostProcessor(rpcClientConfig);
    }
}
