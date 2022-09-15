package com.sunchaser.shushan.rpc.core.compress;

import com.google.common.collect.Maps;
import com.sunchaser.shushan.rpc.core.compress.impl.SnappyCompressor;
import com.sunchaser.shushan.rpc.core.extension.ExtensionLoader;
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
public class CompressSupport {

    private CompressSupport() {
    }

    private static final Map<Byte, Compressor> ID_COMPRESSOR_MAP = Maps.newHashMap();

    static {
        ExtensionLoader<Compressor> extensionLoader = ExtensionLoader.getExtensionLoader(Compressor.class);
        Set<String> supportedExtensions = extensionLoader.getSupportedExtensions();
        for (String name : supportedExtensions) {
            Compressor compressor = extensionLoader.getExtension(name);
            byte id = compressor.getContentTypeId();
            Compressor oldCompressor = ID_COMPRESSOR_MAP.get(id);
            if (Objects.nonNull(oldCompressor)) {
                LOGGER.error("Compressor extension " + compressor.getClass().getName()
                        + " has duplicate id to Compressor extension "
                        + oldCompressor.getClass().getName()
                        + ", ignore this Compressor extension");
                continue;
            }
            ID_COMPRESSOR_MAP.put(id, compressor);
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
     */
    public static Compressor getCompressor(byte compress) {
        return ID_COMPRESSOR_MAP.getOrDefault(compress, new SnappyCompressor());
    }
}
