package com.shopease.admin.user.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopease.admin.FileUploadUtil;
import com.shopease.admin.security.ShopeaseUserDetails;
import com.shopease.admin.user.UserService;
import com.shopease.common.entity.User;

import org.springframework.util.StringUtils;

import org.springframework.ui.Model;

@Controller
public class AccountController {
	
	@Autowired
	private UserService service;
	
	@GetMapping("/account")
	public String viewDetails(@AuthenticationPrincipal ShopeaseUserDetails loggedUser, 
			Model model) {
		String email = loggedUser.getUsername();
		User user = service.getByEmail(email);
		
		model.addAttribute("user", user);
		
		return "users/account_form";
		
	}
	
	
	@PostMapping("/account/update")
	public String saveDetails(User user, RedirectAttributes redirectAttributes,
			@AuthenticationPrincipal ShopeaseUserDetails loggedInUser, 
			@RequestParam("image") MultipartFile multipartFile) throws IOException {
		
		if (!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			user.setPhotos(fileName);
			User savedUser = service.updateAccount(user);
			
			String uploadDir = "user-photos/" + savedUser.getId();
			
			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
			
		} else {
			if (user.getPhotos().isEmpty()) user.setPhotos(null);
			service.updateAccount(user);
		}

		loggedInUser.setFirstName(user.getFirstName());
		loggedInUser.setLastName(user.getFirstName());
		
		redirectAttributes.addFlashAttribute("message", "Your account has been updated.");
		
		return "redirect:/account";
	}
}
