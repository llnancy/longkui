/*
 * Copyright 2022 LongKui
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.llnancy.longkui.core.util;

import lombok.SneakyThrows;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

/**
 * IO util
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/20
 */
public final class IoUtils {

    private IoUtils() {
    }

    /**
     * 默认缓冲区大小 8192
     */
    public static final int DEFAULT_BUFFER_SIZE = 2 << 12;

    /**
     * 数据流末尾
     */
    public static final int EOF = -1;

    /**
     * 拷贝流，拷贝后不关闭流
     *
     * @param in  输入流
     * @param out 输出流
     */
    public static void copy(InputStream in, OutputStream out) {
        copy(in, out, DEFAULT_BUFFER_SIZE);
    }

    /**
     * 拷贝流，拷贝后不关闭流
     *
     * @param in         输入流
     * @param out        输出流
     * @param bufferSize 缓存大小
     */
    @SneakyThrows
    public static void copy(InputStream in, OutputStream out, int bufferSize) {
        Objects.requireNonNull(in, "InputStream is null !");
        Objects.requireNonNull(out, "OutputStream is null !");
        if (bufferSize <= 0) {
            bufferSize = DEFAULT_BUFFER_SIZE;
        }

        byte[] buffer = new byte[bufferSize];
        int len;
        while ((len = in.read(buffer)) != EOF) {
            out.write(buffer, 0, len);
        }
        out.flush();
    }
}
