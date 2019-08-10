package com.miyako.subject.service.course.consumer.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * ClassName WebConfig
 * Description //TODO
 * Author weila
 * Date 2019-08-10-0010 13:44
 */
@Configuration
public class WebConfig implements WebMvcConfigurer{

    private static Logger log = LoggerFactory.getLogger(WebConfig.class);

    @Autowired
    StudentArgumentResolver studentArgumentResolver;
    @Autowired
    AuthInterceptor authInterceptor;

    /**
     * 设置一个MiaoshaUser参数给，toList使用
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        //将UserArgumentResolver注册到config里面去
        log.info("addArgumentResolvers");
        argumentResolvers.add(studentArgumentResolver);
    }

    /**
     * 注册拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //注册
        registry.addInterceptor(authInterceptor).addPathPatterns("/**");
        log.info("addInterceptors");
        WebMvcConfigurer.super.addInterceptors(registry);
    }
}
