package com.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.user", "com.course", "com.exam","com.config","com.comm", "com.util", "com.common", "com.example","com.home", "com.stat"})
@MapperScan(basePackages = { "com.stat.mapper"})
public class OnlineEduApplication {

	public static void main(String[] args) {
		SpringApplication.run(OnlineEduApplication.class, args);
	}

}
