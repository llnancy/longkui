package com.sunchaser.shushan.rpc.client.annotation;

import com.sunchaser.shushan.rpc.core.config.RpcServiceConfig;

import java.lang.annotation.*;

/**
 * rpc reference
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/21
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Inherited
public @interface RpcReference {

    String group() default RpcServiceConfig.DEFAULT_GROUP;

    String version() default RpcServiceConfig.DEFAULT_VERSION;
}
