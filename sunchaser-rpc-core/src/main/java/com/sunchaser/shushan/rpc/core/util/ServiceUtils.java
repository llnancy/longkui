package com.sunchaser.shushan.rpc.core.util;

/**
 * service utils
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/21
 */
public class ServiceUtils {

    private ServiceUtils() {
    }

    public static String buildServiceKey(String serviceName, String group, String version) {
        return String.join("#", serviceName, group, version);
    }
}
