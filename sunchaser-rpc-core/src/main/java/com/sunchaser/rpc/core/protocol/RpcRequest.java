package com.sunchaser.rpc.core.protocol;

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
     * 服务版本号
     */
    private String version;

    /**
     * 服务名称
     */
    private String serviceName;

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
}
