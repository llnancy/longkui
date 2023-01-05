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

package io.github.llnancy.longkui.core.common;

import io.github.llnancy.longkui.core.registry.RegistryEnum;

/**
 * 常量
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/8/16
 */
public class Constants {

    public static final String DEFAULT = "default";

    public static final String NETTY = "netty";

    public static final String UNDERLINE = "_";

    public static final String EMPTY = "";

    /**
     * version: 1
     */
    public static final byte DEFAULT_PROTOCOL_VERSION = (byte) 1;

    /**
     * Hessian2
     */
    public static final byte DEFAULT_SERIALIZE = (byte) 0;

    /**
     * Snappy
     */
    public static final byte DEFAULT_COMPRESS = (byte) 1;

    public static final int DEFAULT_CONNECTION_TIMEOUT = 3000;

    public static final int DEFAULT_IO_THREADS = Math.min(Runtime.getRuntime().availableProcessors() + 1, 32);

    /**
     * default registry implementation. zookeeper
     */
    public static final String DEFAULT_REGISTRY = RegistryEnum.ZOOKEEPER.name().toLowerCase();
}
