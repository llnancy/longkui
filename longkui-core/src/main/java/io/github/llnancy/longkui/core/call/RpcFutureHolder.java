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

package io.github.llnancy.longkui.core.call;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Future;

/**
 * thread hold the rpc response future
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/19
 */
@Slf4j
public class RpcFutureHolder {

    private static final ThreadLocal<RpcInvokeFuture<?>> RPC_FUTURE_THREAD_LOCAL = new ThreadLocal<>();

    public static <T> Future<T> getFuture() {
        @SuppressWarnings("unchecked")
        Future<T> future = (Future<T>) RPC_FUTURE_THREAD_LOCAL.get();
        removeFuture();
        return future;
    }

    public static void setFuture(RpcInvokeFuture<?> invokeFuture) {
        RPC_FUTURE_THREAD_LOCAL.set(invokeFuture);
    }

    public static void removeFuture() {
        RPC_FUTURE_THREAD_LOCAL.remove();
    }
}
