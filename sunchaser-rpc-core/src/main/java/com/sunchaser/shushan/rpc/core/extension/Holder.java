package com.sunchaser.shushan.rpc.core.extension;

/**
 * Helper Class for hold a value.
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/14
 */
public class Holder<T> {

    private volatile T value;

    public void set(T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }
}
