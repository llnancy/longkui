package com.sunchaser.shushan.rpc.core.serialize;

import com.google.common.collect.Maps;
import com.sunchaser.shushan.rpc.core.extension.ExtensionLoader;
import com.sunchaser.shushan.rpc.core.serialize.impl.Hessian2Serializer;
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
public class SerializeSupport {

    private SerializeSupport() {
    }

    private static final Map<Byte, Serializer> ID_SERIALIZER_MAP = Maps.newHashMap();

    static {
        ExtensionLoader<Serializer> extensionLoader = ExtensionLoader.getExtensionLoader(Serializer.class);
        Set<String> supportedExtensions = extensionLoader.getSupportedExtensions();
        for (String name : supportedExtensions) {
            Serializer serializer = extensionLoader.getExtension(name);
            byte id = serializer.getContentTypeId();
            Serializer oldSerializer = ID_SERIALIZER_MAP.get(id);
            if (Objects.nonNull(oldSerializer)) {
                LOGGER.error("Compressor extension " + serializer.getClass().getName()
                        + " has duplicate id to Compressor extension "
                        + oldSerializer.getClass().getName()
                        + ", ignore this Compressor extension");
                continue;
            }
            ID_SERIALIZER_MAP.put(id, serializer);
        }
    }

    /**
     * 0 HESSIAN2
     * 1 JSON
     * 2 XML
     * 3 PROTOSTUFF
     * 4 KRYO
     */
    public static Serializer getSerializer(byte serialize) {
        return ID_SERIALIZER_MAP.getOrDefault(serialize, new Hessian2Serializer());
    }
}
