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

import java.util.Objects;
import java.util.stream.IntStream;

/**
 * A workaround for object arrays serialize with null value error:
 * to use a special value (enum) to denote none/null.
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/19
 */
public enum ArrayElement {

    /**
     * 替换null
     */
    NULL;

    public static void wrapArgs(Object[] args) {
        if (Objects.nonNull(args)) {
            IntStream.range(0, args.length)
                    .filter(i -> Objects.isNull(args[i]))
                    .forEach(i -> args[i] = ArrayElement.NULL);
        }
    }

    public static void unwrapArgs(Object[] args) {
        if (Objects.nonNull(args)) {
            IntStream.range(0, args.length)
                    .filter(i -> args[i] == ArrayElement.NULL)
                    .forEach(i -> args[i] = null);
        }
    }
}
