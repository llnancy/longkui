package com.sunchaser.shushan.rpc.demo.server;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * demo provider
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/21
 */
@SpringBootApplication
public class DemoServerApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(DemoServerApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }
}
