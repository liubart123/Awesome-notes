package com.awesome.awesomenotes.Interation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
// @TestPropertySource("classpath:application-test2.properties")
// @TestPropertySource("/application-test2.properties")
@ActiveProfiles("test")
@Slf4j
public class AsdIT {
    @Value("${securitate.jwtSecret}")
    private String jwtSecret;

    @Test
    void initContext() {
        log.info("init: " + jwtSecret);
    }
}
