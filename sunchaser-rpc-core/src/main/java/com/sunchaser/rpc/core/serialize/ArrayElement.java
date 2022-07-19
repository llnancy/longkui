package com.sunchaser.rpc.core.serialize;

import java.util.Objects;
import java.util.stream.IntStream;

/**
 * A workaround for object arrays serialize with null value error:
 * to use a special value (enum) to denote none/null.
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/19
 */
public enum ArrayElement {

    NULL;

    public static void wrapArgs(Object[] args) {
        if (Objects.nonNull(args)) {
            IntStream.range(0, args.length)
                    .filter(i -> Objects.isNull(args[i]))
                    .forEach(i -> args[i] = ArrayElement.NULL);
        }
    }

    public static void unwrapArgs(Object[] args) {
        if (Objects.nonNull(args)) {
            IntStream.range(0, args.length)
                    .filter(i -> args[i] == ArrayElement.NULL)
                    .forEach(i -> args[i] = null);
        }
    }
}
