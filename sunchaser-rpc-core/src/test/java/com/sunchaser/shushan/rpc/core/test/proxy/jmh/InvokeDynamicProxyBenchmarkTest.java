package com.sunchaser.shushan.rpc.core.test.proxy.jmh;

import com.sunchaser.shushan.rpc.core.config.RpcServiceConfig;
import com.sunchaser.shushan.rpc.core.test.HelloService;
import com.sunchaser.shushan.rpc.core.test.proxy.jmh.impl.JMHByteBuddyRpcDynamicProxy;
import com.sunchaser.shushan.rpc.core.test.proxy.jmh.impl.JMHCglibRpcDynamicProxy;
import com.sunchaser.shushan.rpc.core.test.proxy.jmh.impl.JMHJavassistRpcDynamicProxy;
import com.sunchaser.shushan.rpc.core.test.proxy.jmh.impl.JMHJdkRpcDynamicProxy;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * 动态代理对象调用方法 JMH 基准测试
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/8/24
 */
@BenchmarkMode(Mode.AverageTime)// 统计模式
@OutputTimeUnit(TimeUnit.NANOSECONDS)// 统计单位
@State(Scope.Benchmark)
@Warmup(iterations = 3, time = 10)// 预热3轮，每轮10秒
@Measurement(iterations = 3, time = 10)// 度量3轮，每轮10秒
@Fork(1)
@Threads(8)
public class InvokeDynamicProxyBenchmarkTest {

    private static final String JDK = "jdk";

    private static final String CGLIB = "cglib";

    private static final String JAVASSIST = "javassist";

    private static final String BYTE_BUDDY = "byteBuddy";

    private static final RpcServiceConfig RPC_SERVICE_CONFIG = RpcServiceConfig.createDefaultConfig(HelloService.class);

    private static final HelloService JDK_PROXY_INSTANCE = JMHJdkRpcDynamicProxy.getInstance().createProxyInstance(RPC_SERVICE_CONFIG);

    private static final HelloService CGLIB_PROXY_INSTANCE = JMHCglibRpcDynamicProxy.getInstance().createProxyInstance(RPC_SERVICE_CONFIG);

    private static final HelloService JAVASSIST_PROXY_INSTANCE = JMHJavassistRpcDynamicProxy.getInstance().createProxyInstance(RPC_SERVICE_CONFIG);

    private static final HelloService BYTE_BUDDY_PROXY_INSTANCE = JMHByteBuddyRpcDynamicProxy.getInstance().createProxyInstance(RPC_SERVICE_CONFIG);

    @Benchmark
    public String jdk() {
        return JDK_PROXY_INSTANCE.sayHello(JDK);
    }

    @Benchmark
    public String cglib() {
        return CGLIB_PROXY_INSTANCE.sayHello(CGLIB);
    }

    @Benchmark
    public String javassist() {
        return JAVASSIST_PROXY_INSTANCE.sayHello(JAVASSIST);
    }

    @Benchmark
    public String byteBuddy() {
        return BYTE_BUDDY_PROXY_INSTANCE.sayHello(BYTE_BUDDY);
    }

    public static void main(String[] args) throws Exception {
        Options options = new OptionsBuilder()
                .include(InvokeDynamicProxyBenchmarkTest.class.getSimpleName())
                .result("invoke-jmh-result.json")
                .resultFormat(ResultFormatType.JSON)
                .build();
        new Runner(options).run();
    }
}
