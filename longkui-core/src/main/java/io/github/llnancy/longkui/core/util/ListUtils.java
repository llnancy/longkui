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

package io.github.llnancy.longkui.core.util;

import java.util.List;
import java.util.Objects;

/**
 * List util
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/9
 */
public final class ListUtils {

    private ListUtils() {
    }

    public static <T> T get(final List<T> list, final int index, final T defaultValue) {
        return isListIndexValid(list, index) ? list.get(index) : defaultValue;
    }

    public static <T> boolean isListIndexValid(final List<T> list, final int index) {
        return index >= 0 && getLength(list) > index;
    }

    public static <T> int getLength(List<T> list) {
        if (Objects.isNull(list)) {
            return 0;
        }
        return list.size();
    }
}
