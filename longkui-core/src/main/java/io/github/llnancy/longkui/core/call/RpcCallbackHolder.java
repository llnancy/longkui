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

/**
 * thread hold the rpc invoke callback
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/20
 */
public class RpcCallbackHolder {

    private static final ThreadLocal<RpcCallback<?>> RPC_INVOKE_CALLBACK_THREAD_LOCAL = new ThreadLocal<>();

    public static RpcCallback<?> getCallback() {
        RpcCallback<?> rpcCallback = RPC_INVOKE_CALLBACK_THREAD_LOCAL.get();
        removeCallback();
        return rpcCallback;
    }

    public static void setCallback(RpcCallback<?> rpcCallback) {
        RPC_INVOKE_CALLBACK_THREAD_LOCAL.set(rpcCallback);
    }

    public static void removeCallback() {
        RPC_INVOKE_CALLBACK_THREAD_LOCAL.remove();
    }
}
