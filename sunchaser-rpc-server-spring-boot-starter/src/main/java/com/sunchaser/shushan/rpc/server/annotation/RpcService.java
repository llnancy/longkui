package com.sunchaser.shushan.rpc.server.annotation;

import com.sunchaser.shushan.rpc.core.balancer.Weightable;
import com.sunchaser.shushan.rpc.core.config.RpcServiceConfig;
import org.springframework.stereotype.Service;

import java.lang.annotation.*;

/**
 * rpc service annotation
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/21
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Service
public @interface RpcService {

    String group() default RpcServiceConfig.DEFAULT_GROUP;

    String version() default RpcServiceConfig.DEFAULT_VERSION;

    int weight() default Weightable.DEFAULT_WEIGHT;

    int warmup() default Weightable.DEFAULT_WARMUP;
}
