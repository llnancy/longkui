/*
 * Copyright 2022 SunChaser
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

package com.sunchaser.shushan.rpc.core.compress.impl;

import com.sunchaser.shushan.rpc.core.util.IoUtils;
import lombok.SneakyThrows;
import org.anarres.lzo.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * 基于lzo（Lempel-Ziv-Oberhumer）算法实现的压缩与解压缩
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/20
 */
public class LzoCompressor extends AbstractCompressor {

    /**
     * Get type unique id
     *
     * @return type id
     */
    @Override
    public byte getTypeId() {
        return (byte) 6;
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
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             LzoOutputStream lzoOs = new LzoOutputStream(bos, LzoLibrary.getInstance().newCompressor(LzoAlgorithm.LZO1X, null))) {
            lzoOs.write(data);
            return bos.toByteArray();
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
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             LzoInputStream lzoIs = new LzoInputStream(new ByteArrayInputStream(data), LzoLibrary.getInstance().newDecompressor(LzoAlgorithm.LZO1X, LzoConstraint.SPEED))) {
            IoUtils.copy(lzoIs, bos);
            return bos.toByteArray();
        }
    }
}
