package com.reverse.proxy.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication(scanBasePackages = {"com.reverse.proxy.demo"})
@EnableJpaAuditing
public class HTTPProxyMain {

	public static void main(String[] args) {
		SpringApplication.run(HTTPProxyMain.class, args);
	}
}
