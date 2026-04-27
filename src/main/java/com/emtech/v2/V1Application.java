package com.emtech.v2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class V1Application extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(V1Application.class, args);
    }
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(V1Application.class);
    }

}
