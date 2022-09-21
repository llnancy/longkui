package com.sunchaser.shushan.rpc.demo.client;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * demo consumer
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/9/22
 */
@SpringBootApplication
public class DemoClientApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(DemoClientApplication.class)
                .web(WebApplicationType.SERVLET)
                .run(args);
    }
}
