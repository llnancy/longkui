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

package io.github.llnancy.longkui.core.serialize;

import com.google.common.collect.Maps;
import io.github.llnancy.longkui.core.extension.ExtensionLoader;
import io.github.llnancy.longkui.core.serialize.impl.Hessian2Serializer;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * SPI Serialize Support
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/15
 */
@Slf4j
public final class SerializeSupport {

    private SerializeSupport() {
    }

    private static final Map<Byte, Serializer> ID_SERIALIZER_MAP = Maps.newHashMap();

    static {
        ExtensionLoader<Serializer> extensionLoader = ExtensionLoader.getExtensionLoader(Serializer.class);
        Set<String> supportedExtensions = extensionLoader.getSupportedExtensions();
        for (String name : supportedExtensions) {
            Serializer serializer = extensionLoader.getExtension(name);
            byte typeId = serializer.getTypeId();
            Serializer oldSerializer = ID_SERIALIZER_MAP.get(typeId);
            if (Objects.nonNull(oldSerializer)) {
                LOGGER.error("Compressor extension " + serializer.getClass().getName()
                        + " has duplicate id to Compressor extension "
                        + oldSerializer.getClass().getName()
                        + ", ignore this Compressor extension");
                continue;
            }
            ID_SERIALIZER_MAP.put(typeId, serializer);
        }
    }

    /**
     * 0 HESSIAN2
     * 1 JSON
     * 2 XML
     * 3 PROTOSTUFF
     * 4 KRYO
     *
     * @param serialize serialize byte
     * @return Serializer
     */
    public static Serializer getSerializer(byte serialize) {
        return ID_SERIALIZER_MAP.getOrDefault(serialize, new Hessian2Serializer());
    }
}
