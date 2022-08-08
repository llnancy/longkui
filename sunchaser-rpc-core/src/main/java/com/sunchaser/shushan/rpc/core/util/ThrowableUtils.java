package com.sunchaser.shushan.rpc.core.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 异常工具类
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/13
 */
public class ThrowableUtils {

    private ThrowableUtils() {
    }

    public static String toString(Throwable t) {
        StringWriter writer = new StringWriter();
        t.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }
}
