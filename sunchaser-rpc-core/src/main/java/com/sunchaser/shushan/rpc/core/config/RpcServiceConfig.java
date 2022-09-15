package com.sunchaser.shushan.rpc.core.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * rpc service config
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RpcServiceConfig {

    /**
     * 默认版本号
     */
    public static final String DEFAULT_VERSION = "0.0.0";

    /**
     * 默认分组
     */
    public static final String DEFAULT_GROUP = "default";

    /**
     * 默认超时时间
     */
    public static final long DEFAULT_TIMEOUT = 0L;

    /**
     * rpc服务提供类Class（目标代理类的Class）
     */
    private Class<?> targetClass;

    /**
     * rpc服务版本号
     */
    private String version = DEFAULT_VERSION;

    /**
     * rpc服务分组
     */
    private String group = DEFAULT_GROUP;

    /**
     * rpc调用超时时间
     */
    private long timeout = DEFAULT_TIMEOUT;

    public String getClassName() {
        return this.targetClass.getName();
    }

    public String getRpcServiceKey() {
        return String.join("#", this.getClassName(), version, group);
    }

    public static RpcServiceConfig createDefaultConfig() {
        return new RpcServiceConfig();
    }

    public static RpcServiceConfig createDefaultConfig(Class<?> clazz) {
        return createDefaultConfig().setTargetClass(clazz);
    }
}
