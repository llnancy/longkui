/*
 * Copyright 2023 LongKui
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

package io.github.llnancy.longkui.core.uid.impl;

import cn.hutool.core.lang.Snowflake;
import io.github.llnancy.longkui.core.uid.SequenceIdGenerator;

/**
 * an id generator implementation based on Snowflake
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/16
 */
public class SnowflakeIdGenerator implements SequenceIdGenerator {

    private final Snowflake snowflake;

    public SnowflakeIdGenerator() {
        this(0, 0);
    }

    public SnowflakeIdGenerator(long workerId, long dataCenterId) {
        this.snowflake = new Snowflake(workerId, dataCenterId);
    }

    /**
     * 获取下一个SequenceId
     *
     * @return long
     */
    @Override
    public long nextSequenceId() {
        return this.snowflake.nextId();
    }
}
