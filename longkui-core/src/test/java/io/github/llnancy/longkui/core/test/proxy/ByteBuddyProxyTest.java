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

package io.github.llnancy.longkui.core.test.proxy;

import io.github.llnancy.longkui.core.test.HelloService;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.Method;

/**
 * byte buddy proxy test
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/8/25
 */
@Slf4j
public class ByteBuddyProxyTest {

    public static class ByteBuddyMethodHandler {

        @RuntimeType
        public Object byteBuddyInvoke(@This Object proxy,@Origin Method method, @AllArguments @RuntimeType Object[] args) throws Throwable {
            return "byte buddy Proxy invoke";
        }
    }

    public static void main(String[] args) throws Exception {
        HelloService helloService = new ByteBuddy().subclass(HelloService.class)
                .method(ElementMatchers.isDeclaredBy(HelloService.class))// 拦截哪些方法
                .intercept(MethodDelegation.to(new ByteBuddyMethodHandler()))// 拦截方法处理器
                .make()
                .load(HelloService.class.getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                .getLoaded()
                .getDeclaredConstructor()
                .newInstance();
        String hi = helloService.sayHello("hi");
        LOGGER.info("hi: {}", hi);
    }
}
