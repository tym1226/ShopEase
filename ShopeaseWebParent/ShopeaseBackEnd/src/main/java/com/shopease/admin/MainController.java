package com.shopease.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
	@GetMapping("")
	public String viewHomePage() {
		return "index.html";
	}
	
	@GetMapping("/login") 
	public String loginPage() {
		return "login.html";
	}
}
