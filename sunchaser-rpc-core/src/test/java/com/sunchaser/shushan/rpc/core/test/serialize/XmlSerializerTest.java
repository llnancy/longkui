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

package com.sunchaser.shushan.rpc.core.test.serialize;

import com.sunchaser.shushan.rpc.core.protocol.RpcRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

/**
 * XmlSerializer Test
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/8
 */
@Slf4j
public class XmlSerializerTest {

    protected static final RpcRequest REQUEST = RpcRequest.builder()
            .serviceName("com.sunchaser.shushan.rpc.core.test.HelloService")
            .methodName("sayHello")
            .version("1")
            .argTypes(new Class[]{String.class, null, Integer.class})
            .args(new Object[]{"hello, sunchaser", null, 666})
            .build();

    @Test
    void serialize() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        XMLEncoder xmlEncoder = new XMLEncoder(bos, StandardCharsets.UTF_8.name(), true, 0);
        xmlEncoder.writeObject("XXXX");
        byte[] serialize = bos.toByteArray();
        xmlEncoder.close();
        ByteArrayInputStream bis = new ByteArrayInputStream(serialize);
        XMLDecoder xmlDecoder = new XMLDecoder(bis);
        Object obj = xmlDecoder.readObject();
        xmlDecoder.close();
        String request = (String) obj;
        LOGGER.info("deserialize: {}", request);
    }
}
