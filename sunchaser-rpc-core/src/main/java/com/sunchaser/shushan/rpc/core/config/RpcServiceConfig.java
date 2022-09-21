package com.sunchaser.shushan.rpc.core.config;

import com.sunchaser.shushan.rpc.core.balancer.Weightable;
import com.sunchaser.shushan.rpc.core.call.CallType;
import com.sunchaser.shushan.rpc.core.common.Constants;
import com.sunchaser.shushan.rpc.core.util.ServiceUtils;
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
    public static final String DEFAULT_GROUP = Constants.DEFAULT;

    /**
     * 默认超时时间
     */
    public static final Long DEFAULT_TIMEOUT = 0L;

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
     * rpc service node weight
     */
    private Integer weight = Weightable.DEFAULT_WEIGHT;

    /**
     * rpc service node warmup time
     */
    private Integer warmup = Weightable.DEFAULT_WARMUP;

    /**
     * rpc调用超时时间
     */
    private Long timeout = DEFAULT_TIMEOUT;

    /**
     * rpc call type, default SYNC
     */
    private CallType callType = CallType.SYNC;

    public String getClassName() {
        return this.targetClass.getName();
    }

    public String getRpcServiceKey() {
        return ServiceUtils.buildServiceKey(this.getClassName(), this.group, this.version);
    }

    public static RpcServiceConfig createDefaultConfig(Class<?> clazz) {
        return new RpcServiceConfig().setTargetClass(clazz);
    }
}
