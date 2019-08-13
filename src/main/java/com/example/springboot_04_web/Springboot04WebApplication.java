package com.example.springboot_04_web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

import java.util.Locale;

@SpringBootApplication
public class Springboot04WebApplication {

	public static void main(String[] args) {
		SpringApplication.run(Springboot04WebApplication.class, args);
	}

//	@Bean
//	public ViewResolver myViewResolver(){
//		return myViewResolver();
//	}
//
//	public static class myViewResolver implements ViewResolver{
//
//		@Override
//		public View resolveViewName(String s, Locale locale) throws Exception {
//			return null;
//		}
//	}
}
