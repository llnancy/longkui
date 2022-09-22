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

package com.sunchaser.shushan.rpc.core.balancer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Node
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Node<T> {

    /**
     * 节点
     */
    private T node;

    /**
     * 时间戳
     */
    private long timestamp;
}
