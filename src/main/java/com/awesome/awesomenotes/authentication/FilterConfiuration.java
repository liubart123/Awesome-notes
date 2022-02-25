package com.awesome.awesomenotes.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfiuration {

    @Autowired
    AuthService authService;

    @Autowired
    ObjectMapper objectMapper;

    @Bean
    public FilterRegistrationBean<AuthTokenFilter> authFilter() {
        FilterRegistrationBean<AuthTokenFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(new AuthTokenFilter(authService, objectMapper));
        registrationBean.addUrlPatterns("/api/auth/current");
        registrationBean.addUrlPatterns("/api/auth/admin");
        registrationBean.addUrlPatterns("/api/auth/moderator");
        registrationBean.addUrlPatterns("/api/users/*");
        registrationBean.addUrlPatterns("/api/notes/*");
        registrationBean.setOrder(1);
        registrationBean.setName("auth");

        return registrationBean;
    }
}
