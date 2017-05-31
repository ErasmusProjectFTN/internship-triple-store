package com.ErasmusProject.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@EnableDiscoveryClient
@SpringBootApplication
@ComponentScan(basePackages = { "com.ErasmusProject.app","com.ErasmusProject.rest", "com.ErasmusProject.util"})
public class InternshipTripleStoreServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InternshipTripleStoreServiceApplication.class, args);
	}
}
