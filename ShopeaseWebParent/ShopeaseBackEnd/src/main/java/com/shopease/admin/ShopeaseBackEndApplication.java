package com.shopease.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan({"com.shopease.common.entity", "com.shopease.admin.user"})
public class ShopeaseBackEndApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShopeaseBackEndApplication.class, args);
	}

}
