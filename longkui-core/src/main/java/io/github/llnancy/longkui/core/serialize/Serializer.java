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

package io.github.llnancy.longkui.core.serialize;

import io.github.llnancy.longkui.core.extension.SPI;
import io.github.llnancy.longkui.core.util.TypeId;

/**
 * 序列化器
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/12
 */
@SPI
public interface Serializer extends TypeId {

    /**
     * 将对象进行序列化
     *
     * @param obj 待序列化的对象
     * @param <T> 对象泛型类型
     * @return 序列化后的byte字节数组
     */
    <T> byte[] serialize(T obj);

    /**
     * 将二进制字节数组进行反序列化
     *
     * @param data  二进制字节数组
     * @param clazz 待反序列化的class类型
     * @param <T>   泛型类型
     * @return 反序列化后的对象
     */
    <T> T deserialize(byte[] data, Class<T> clazz);
}
