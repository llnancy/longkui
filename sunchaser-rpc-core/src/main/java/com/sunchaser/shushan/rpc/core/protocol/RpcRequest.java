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

package com.sunchaser.shushan.rpc.core.protocol;

import com.sunchaser.shushan.rpc.core.util.ServiceUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * rpc request 协议消息请求体
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RpcRequest implements Serializable {

    private static final long serialVersionUID = -4725744482727139470L;

    /**
     * 服务名称（全限定类名）
     */
    private String serviceName;

    /**
     * 服务版本号
     */
    private String version;

    /**
     * 服务分组
     */
    private String group;

    /**
     * 方法名称
     */
    private String methodName;

    /**
     * 方法参数类型列表
     */
    private Class<?>[] argTypes;

    /**
     * 方法参数列表
     */
    private Object[] args;

    public String getRpcServiceKey() {
        return ServiceUtils.buildServiceKey(serviceName, group, version);
    }
}
