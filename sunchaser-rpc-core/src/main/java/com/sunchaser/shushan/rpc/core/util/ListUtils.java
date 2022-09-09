package com.sunchaser.shushan.rpc.core.util;

import java.util.List;
import java.util.Objects;

/**
 * List util
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/9
 */
public class ListUtils {

    private ListUtils() {
    }

    public static <T> T get(final List<T> list, final int index, final T defaultValue) {
        return isListIndexValid(list, index) ? list.get(index) : defaultValue;
    }

    public static <T> boolean isListIndexValid(final List<T> list, final int index) {
        return index >= 0 && getLength(list) > index;
    }

    public static <T> int getLength(List<T> list) {
        if (Objects.isNull(list)) {
            return 0;
        }
        return list.size();
    }
}
