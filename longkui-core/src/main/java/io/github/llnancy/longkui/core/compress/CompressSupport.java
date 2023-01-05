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

package io.github.llnancy.longkui.core.compress;

import com.google.common.collect.Maps;
import io.github.llnancy.longkui.core.compress.impl.SnappyCompressor;
import io.github.llnancy.longkui.core.extension.ExtensionLoader;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * SPI Compress Support
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/15
 */
@Slf4j
public final class CompressSupport {

    private CompressSupport() {
    }

    private static final Map<Byte, Compressor> ID_COMPRESSOR_MAP = Maps.newHashMap();

    static {
        ExtensionLoader<Compressor> extensionLoader = ExtensionLoader.getExtensionLoader(Compressor.class);
        Set<String> supportedExtensions = extensionLoader.getSupportedExtensions();
        for (String name : supportedExtensions) {
            Compressor compressor = extensionLoader.getExtension(name);
            byte typeId = compressor.getTypeId();
            Compressor oldCompressor = ID_COMPRESSOR_MAP.get(typeId);
            if (Objects.nonNull(oldCompressor)) {
                LOGGER.error("Compressor extension " + compressor.getClass().getName()
                        + " has duplicate id to Compressor extension "
                        + oldCompressor.getClass().getName()
                        + ", ignore this Compressor extension");
                continue;
            }
            ID_COMPRESSOR_MAP.put(typeId, compressor);
        }
    }

    /**
     * 0 None
     * 1 Snappy
     * 2 DEFLATE
     * 3 Gzip
     * 4 bzip2
     * 5 LZ4
     * 6 LZO
     *
     * @param compress compress byte
     * @return Compressor
     */
    public static Compressor getCompressor(byte compress) {
        return ID_COMPRESSOR_MAP.getOrDefault(compress, new SnappyCompressor());
    }
}
