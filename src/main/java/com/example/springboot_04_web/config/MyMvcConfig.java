package com.example.springboot_04_web.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

//WebMvcConfigurerAdapter可以扩展SpringMVC的功能
@Configuration
//command+o 重写方法
public class MyMvcConfig implements WebMvcConfigurer {


    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        //super.addViewControllers(registry);
//        浏览器发送/reselect请求来到success页面
        registry.addViewController("/reselect").setViewName("success");
    }
}

