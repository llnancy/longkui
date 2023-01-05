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

package io.github.llnancy.longkui.core.test.serialize;

import io.github.llnancy.longkui.core.serialize.Serializer;
import io.github.llnancy.longkui.core.serialize.impl.Hessian2Serializer;
import lombok.extern.slf4j.Slf4j;

/**
 * Hessian2Serializer Test
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/8
 */
@Slf4j
public class Hessian2SerializerTest extends AbstractSerializerTest {

    @Override
    protected Serializer getSerializer() {
        return new Hessian2Serializer();
    }
}
