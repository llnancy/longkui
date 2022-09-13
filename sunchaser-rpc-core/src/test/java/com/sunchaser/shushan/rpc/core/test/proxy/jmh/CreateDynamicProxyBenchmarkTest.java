package com.sunchaser.shushan.rpc.core.test.proxy.jmh;

import com.sunchaser.shushan.rpc.core.config.RpcServiceConfig;
import com.sunchaser.shushan.rpc.core.test.HelloService;
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

    @Param({"jdk", "cglib", "javassist", "byteBuddy"})
    private String proxyType;

    @Benchmark
    public HelloService create() {
        return JMHRpcDynamicProxyFactory.getRpcProxyInstance(proxyType, RpcServiceConfig.createDefaultConfig(HelloService.class));
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
