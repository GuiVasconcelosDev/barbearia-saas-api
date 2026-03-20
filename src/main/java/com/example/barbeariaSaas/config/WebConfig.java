package com.example.barbeariaSaas.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class WebConfig implements WebMvcConfigurer{

    @Autowired
    private SegurancaInterceptor segurancaInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Coloca o Segurança a vigiar TUDO o que começa com /api/
        registry.addInterceptor(segurancaInterceptor).addPathPatterns("/api/**");
    }
}
