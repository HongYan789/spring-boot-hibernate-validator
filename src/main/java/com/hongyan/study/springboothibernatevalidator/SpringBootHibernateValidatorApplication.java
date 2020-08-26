package com.hongyan.study.springboothibernatevalidator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

@SpringBootApplication
public class SpringBootHibernateValidatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootHibernateValidatorApplication.class, args);
	}

	@Bean
	public MethodValidationPostProcessor methodValidationPostProcessor() {
	  /**默认是普通模式，会返回所有的验证不通过信息集合*/
		return new MethodValidationPostProcessor();
	}

}
