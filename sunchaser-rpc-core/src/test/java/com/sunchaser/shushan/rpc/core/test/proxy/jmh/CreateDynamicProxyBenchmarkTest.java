package com.sunchaser.shushan.rpc.core.test.proxy.jmh;

import com.sunchaser.shushan.rpc.core.config.RpcFrameworkConfig;
import com.sunchaser.shushan.rpc.core.config.RpcServiceConfig;
import com.sunchaser.shushan.rpc.core.test.HelloService;
import com.sunchaser.shushan.rpc.core.test.proxy.jmh.impl.JMHByteBuddyDynamicProxy;
import com.sunchaser.shushan.rpc.core.test.proxy.jmh.impl.JMHCglibDynamicProxy;
import com.sunchaser.shushan.rpc.core.test.proxy.jmh.impl.JMHJavassistDynamicProxy;
import com.sunchaser.shushan.rpc.core.test.proxy.jmh.impl.JMHJdkDynamicProxy;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * 创建代理对象 JMH 基准测试
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/8/24
 */
@BenchmarkMode(Mode.AverageTime)// 统计模式
@OutputTimeUnit(TimeUnit.NANOSECONDS)// 统计单位
@State(Scope.Thread)
@Warmup(iterations = 3, time = 10)// 预热3轮，每轮10秒
@Measurement(iterations = 3, time = 10)// 度量3轮，每轮10秒
@Fork(1)
public class CreateDynamicProxyBenchmarkTest {

    private static final RpcFrameworkConfig RPC_FRAMEWORK_CONFIG = RpcFrameworkConfig.createDefaultConfig(HelloService.class);

    @Benchmark
    public HelloService jdkCreate() {
        return JMHJdkDynamicProxy.getInstance().createProxyInstance(RPC_FRAMEWORK_CONFIG);
    }

    @Benchmark
    public HelloService cglibCreate() {
        return JMHCglibDynamicProxy.getInstance().createProxyInstance(RPC_FRAMEWORK_CONFIG);
    }

    @Benchmark
    public HelloService javassistCreate() {
        return JMHJavassistDynamicProxy.getInstance().createProxyInstance(RPC_FRAMEWORK_CONFIG);
    }

    @Benchmark
    public HelloService byteBuddyCreate() {
        return JMHByteBuddyDynamicProxy.getInstance().createProxyInstance(RPC_FRAMEWORK_CONFIG);
    }

    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(CreateDynamicProxyBenchmarkTest.class.getSimpleName())
                .result("create-jmh-result.json")
                .resultFormat(ResultFormatType.JSON)
                .build();
        new Runner(options).run();
    }
}
