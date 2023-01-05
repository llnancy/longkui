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

package io.github.llnancy.longkui.core.compress.impl;

import io.github.llnancy.longkui.core.compress.Compressor;

import java.util.Objects;

/**
 * an abstract compressor impl
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/20
 */
public abstract class AbstractCompressor implements Compressor {

    /**
     * 将数据进行压缩
     *
     * @param data 原比特数组
     * @return 压缩后的数据
     */
    @Override
    public byte[] compress(byte[] data) {
        Objects.requireNonNull(data, "compress data is null");
        return doCompress(data);
    }

    /**
     * 将数据解压缩
     *
     * @param data 压缩的数据
     * @return 原数据
     */
    @Override
    public byte[] unCompress(byte[] data) {
        Objects.requireNonNull(data, "unCompress data is null");
        return doUnCompress(data);
    }

    /**
     * 将数据进行压缩
     *
     * @param data 原比特数组
     * @return 压缩后的数据
     */
    protected abstract byte[] doCompress(byte[] data);

    /**
     * 将数据解压缩
     *
     * @param data 压缩的数据
     * @return 原数据
     */
    protected abstract byte[] doUnCompress(byte[] data);
}
