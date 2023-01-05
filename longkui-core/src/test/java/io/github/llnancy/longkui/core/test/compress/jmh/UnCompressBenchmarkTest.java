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

package io.github.llnancy.longkui.core.test.compress.jmh;

import io.github.llnancy.longkui.core.compress.Compressor;
import io.github.llnancy.longkui.core.compress.impl.Bzip2Compressor;
import io.github.llnancy.longkui.core.compress.impl.DeflateCompressor;
import io.github.llnancy.longkui.core.compress.impl.GzipCompressor;
import io.github.llnancy.longkui.core.compress.impl.Lz4Compressor;
import io.github.llnancy.longkui.core.compress.impl.LzoCompressor;
import io.github.llnancy.longkui.core.compress.impl.SnappyCompressor;
import io.github.llnancy.longkui.core.protocol.RpcRequest;
import io.github.llnancy.longkui.core.serialize.Serializer;
import io.github.llnancy.longkui.core.serialize.impl.JsonSerializer;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/8
 */
@BenchmarkMode(Mode.AverageTime)// 统计模式
@OutputTimeUnit(TimeUnit.NANOSECONDS)// 统计单位
@State(Scope.Benchmark)
@Warmup(iterations = 3, time = 3)// 预热3轮，每轮10秒
@Measurement(iterations = 3, time = 3)// 度量3轮，每轮10秒
@Fork(1)
@Threads(8)
public class UnCompressBenchmarkTest {

    private Compressor bzip2;

    private Compressor deflate;

    private Compressor gzip;

    private Compressor lz4;

    private Compressor lzo;

    private Compressor snappy;

    private byte[] bzip2Data;

    private byte[] deflateData;

    private byte[] gzipData;

    private byte[] lz4Data;

    private byte[] lzoData;

    private byte[] snappyData;

    @Setup
    public void prepare() {
        bzip2 = new Bzip2Compressor();
        deflate = new DeflateCompressor();
        gzip = new GzipCompressor();
        lzo = new LzoCompressor();
        lz4 = new Lz4Compressor();
        snappy = new SnappyCompressor();
        RpcRequest request = RpcRequest.builder()
                .serviceName("io.github.llnancy.longkui.core.test.HelloService")
                .methodName("sayHello")
                .version("1")
                .argTypes(new Class[]{String.class, null, Integer.class})
                .args(new Object[]{"hello, sunchaser", null, 666})
                .build();
        Serializer serializer = new JsonSerializer();
        byte[] data = serializer.serialize(request);
        bzip2Data = bzip2.compress(data);
        deflateData = deflate.compress(data);
        gzipData = gzip.compress(data);
        lzoData = lzo.compress(data);
        lz4Data = lz4.compress(data);
        snappyData = snappy.compress(data);
    }

    @Benchmark
    public byte[] bzip2() {
        return bzip2.unCompress(bzip2Data);
    }

    @Benchmark
    public byte[] deflate() {
        return deflate.unCompress(deflateData);
    }

    @Benchmark
    public byte[] gzip() {
        return gzip.unCompress(gzipData);
    }

    @Benchmark
    public byte[] lz4() {
        return lz4.unCompress(lz4Data);
    }

    @Benchmark
    public byte[] lzo() {
        return lzo.unCompress(lzoData);
    }

    @Benchmark
    public byte[] snappy() {
        return snappy.unCompress(snappyData);
    }

    public static void main(String[] args) throws Exception {
        Options options = new OptionsBuilder()
                .include(UnCompressBenchmarkTest.class.getSimpleName())
                .result("uncompress-jmh-result.json")
                .resultFormat(ResultFormatType.JSON)
                .build();
        new Runner(options).run();
    }
}
