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

package io.github.llnancy.longkui.core.transport.client;

import com.google.common.collect.Maps;
import io.netty.channel.Channel;

import java.util.Objects;
import java.util.concurrent.ConcurrentMap;

/**
 * netty channel pool
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/13
 */
public class ChannelContainer {

    private static final ConcurrentMap<String, Channel> CHANNEL_POOL = Maps.newConcurrentMap();

    public static void putChannel(String key, Channel channel) {
        CHANNEL_POOL.put(key, channel);
    }

    /**
     * get channel by key
     *
     * @param key host:port
     * @return Channel
     */
    public static Channel getChannel(String key) {
        Channel channel = CHANNEL_POOL.get(key);
        if (Objects.nonNull(channel)) {
            if (channel.isActive()) {
                return channel;
            } else {
                CHANNEL_POOL.remove(key);
            }
        }
        return null;
    }

    public static void removeChannel(String key) {
        CHANNEL_POOL.remove(key);
    }
}
