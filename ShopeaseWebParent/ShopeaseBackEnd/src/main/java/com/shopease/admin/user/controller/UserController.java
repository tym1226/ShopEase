package com.shopease.admin.user.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopease.common.entity.Role;
import com.shopease.common.entity.User;
import com.shopease.admin.FileUploadUtil;
import com.shopease.admin.paging.PagingAndSortingHelper;
import com.shopease.admin.paging.PagingAndSortingParam;
import com.shopease.admin.user.UserNotFoundException;
import com.shopease.admin.user.UserService;


@Controller
public class UserController {
	
	@Autowired
	private UserService service;
	
	private String defaultRedirectURL = "redirect:/users/page/1?sortField=firstName&sortDir=asc";
	
//	@GetMapping("/users")
//	public String listFirstPage(Model model) {
//		return listByPage(1, model, "firstName", "asc", null);
//	}
	
	@GetMapping("/users")
	public String listFirstPage() {
		return defaultRedirectURL;
	}
	
	@GetMapping("/users/page/{pageNum}")
	public String listByPage(
			@PagingAndSortingParam(listName = "listUsers", moduleURL = "/users") PagingAndSortingHelper helper,
			@PathVariable(name = "pageNum") int pageNum) {
		service.listByPage(pageNum, helper);		
		
		return "users/users";		
	}
	
	
//	@GetMapping("/users/page/{pageNumber}") 
//	public String listByPage(@PathVariable (name = "pageNumber") int pageNumber, Model model,
//			@Param("sortField") String sortField, @Param("sortDir") String sortDir, 
//			@Param("keyword") String keyword) {
////		System.out.println("SortField = " + sortField);
////		System.out.println("sortDir = " + sortDir);
//		Page<User> page = service.listByPage(pageNumber, sortField, sortDir, keyword);
//		
//		List<User> listUsers = page.getContent();
//		
////		System.out.println("pageNumber = " + pageNumber);
////		System.out.println("Total elements = " + page.getTotalElements());
////		System.out.println("Total pages = " + page.getTotalPages());
//		
//		long startCount = (pageNumber - 1) * UserService.USERS_PER_PAGE + 1;
//		long endCount = startCount + UserService.USERS_PER_PAGE - 1;
//		
//		if (endCount > page.getTotalElements()) {
//			endCount = page.getTotalElements();
//		}
//		
//		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
//		
//		model.addAttribute("currentPage", pageNumber);
//		model.addAttribute("totalPages", page.getTotalPages());
//		model.addAttribute("startCount", startCount);
//		model.addAttribute("endCount", endCount);
//		model.addAttribute("totalItems", page.getTotalElements());
//		model.addAttribute("listUsers",listUsers);
//		model.addAttribute("sortField", sortField);
//		model.addAttribute("sortDir", sortDir);
//		model.addAttribute("reverseSortDir", reverseSortDir);
//		model.addAttribute("keyword", keyword);
//		return "users/users";
//	}
	
	@GetMapping("/users/new")
	public String newUser(Model model) {
		List<Role> listRoles = service.listRoles();
		
		User user = new User();
		user.setEnabled(true);
		model.addAttribute("user", user);
		model.addAttribute("listRoles", listRoles);
		model.addAttribute("pageTitle", "Create New User");
		return "users/user_form";
	}
	
	@PostMapping("/users/save")
	public String saveUser(User user, RedirectAttributes redirectAttributes,
			@RequestParam("image") MultipartFile multipartFile) throws IOException {
		// System.out.println(user);
		// System.out.println(multipartFile.getOriginalFilename());
		
		if (!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			// persist photo info into db
			user.setPhotos(fileName);
			User savedUser = service.save(user);
			// create directory for each user
			String uploadDir = "user-photos/" + savedUser.getId();
			
			// Clean dir before saving new photos
			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		} else {
			if (user.getPhotos().isEmpty()) user.setPhotos(null);
			service.save(user);
		}
		
		redirectAttributes.addFlashAttribute("message", "The user has been created successfully.");
		
		return getRedirectedToAffectedUser(user);
	}

	private String getRedirectedToAffectedUser(User user) {
		String firstPartOfEmail = user.getEmail().split("@")[0];
		return "redirect:/users/page/1?sortField=id&sortDir=asc&keyword=" + firstPartOfEmail;
	}
	
	@GetMapping("/users/edit/{id}")
	public String editUser(@PathVariable(name = "id") Integer id,
			Model model, 
			RedirectAttributes redirectAttributes) {
		try {
			
			User user = service.get(id);
			List<Role> listRoles = service.listRoles();
			
			String email = user.getEmail();
			String name = user.getFirstName() + " " + user.getLastName();
			
			model.addAttribute("user", user);
			model.addAttribute("pageTitle", "Edit User " + name + "("  + email + ")");
			model.addAttribute("listRoles", listRoles);
			
			return "users/user_form";
			
		} catch (UserNotFoundException ex) {
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
			return "redirect:/users";
		}
		
	}
	
	@GetMapping("/users/delete/{id}")
	public String deleteUser(@PathVariable(name = "id") Integer id,
			Model model, 
			RedirectAttributes redirectAttributes) {
		try {
			User userById = service.get(id);
			String email = userById.getEmail();
			String name = userById.getFirstName() + " " + userById.getLastName();
			service.delete(id);
			redirectAttributes.addFlashAttribute("message", "The user: " + name + "("  + email + ")" +" has been successfully deleted.");
			
		} catch (UserNotFoundException ex) {
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
			
		}
		
		return "redirect:/users";
		
	}

	@GetMapping("/users/{id}/enabled/{status}")
	public String updateUserEnabledStatus(
	        @PathVariable("id") Integer id, 
	        @PathVariable("status") boolean enabled,
	        RedirectAttributes redirectAttributes) {
	    try {
	        service.updateUserEnabledStatus(id, enabled);
	        String status = enabled ? "enabled" : "disabled";
	        
	        User userById = service.get(id);
	        String email = userById.getEmail();
	        String name = userById.getFirstName() + " " + userById.getLastName();
	        String message = "The user: " + name + "("  + email + ")" + " has been " + status;
	        
	        redirectAttributes.addFlashAttribute("message", message);
	    } catch(UserNotFoundException e) {
	        redirectAttributes.addFlashAttribute("errorMessage", "User not found with ID: " + id);
	    }
	    return "redirect:/users";
	}

	
}
