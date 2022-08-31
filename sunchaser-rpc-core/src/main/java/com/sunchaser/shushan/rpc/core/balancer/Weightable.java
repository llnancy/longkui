package com.sunchaser.shushan.rpc.core.balancer;

/**
 * 权重
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/8/31
 */
public interface Weightable {

    /**
     * 获取权重
     *
     * @return 权重值
     */
    default int getWeight() {
        return 1;
    }
}