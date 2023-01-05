/*
 * Copyright 2022 LongKui
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.llnancy.longkui.core.compress.impl;

import io.github.llnancy.longkui.core.util.IoUtils;
import lombok.SneakyThrows;

import java.io.ByteArrayOutputStream;
import java.util.Objects;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * 基于DEFLATE算法实现的压缩与解压缩
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/20
 */
public class DeflateCompressor extends AbstractCompressor {

    /**
     * Get type unique id
     *
     * @return type id
     */
    @Override
    public byte getTypeId() {
        return (byte) 2;
    }

    /**
     * 将数据进行压缩
     *
     * @param data 原比特数组
     * @return 压缩后的数据
     */
    @SneakyThrows
    @Override
    protected byte[] doCompress(byte[] data) {
        Deflater deflater = null;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            deflater = new Deflater(1);
            deflater.setInput(data);
            deflater.finish();
            final byte[] buffer = new byte[IoUtils.DEFAULT_BUFFER_SIZE];
            while (!deflater.finished()) {
                int len = deflater.deflate(buffer);
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } finally {
            if (Objects.nonNull(deflater)) {
                deflater.end();
            }
        }
    }

    /**
     * 将数据解压缩
     *
     * @param data 压缩的数据
     * @return 原数据
     */
    @SneakyThrows
    @Override
    protected byte[] doUnCompress(byte[] data) {
        Inflater inflater = null;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            inflater = new Inflater();
            inflater.setInput(data);
            final byte[] buffer = new byte[IoUtils.DEFAULT_BUFFER_SIZE];
            while (!inflater.finished()) {
                int len = inflater.inflate(buffer);
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } finally {
            if (Objects.nonNull(inflater)) {
                inflater.end();
            }
        }
    }
}
