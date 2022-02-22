package com.awesome.awesomenotes.Interation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@PropertySource("classpath:application-test2.properties ")
@Slf4j
public class AsdIT {
    @Value("${securitate.jwtSecret}")
    private String jwtSecret;

    @Test
    void initContext() {
        log.info("init: " + jwtSecret);
    }
}
