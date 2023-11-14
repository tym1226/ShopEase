package com.shopease.admin.product;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopease.admin.FileUploadUtil;
import com.shopease.admin.brand.BrandService;
import com.shopease.admin.category.CategoryNotFoundException;
import com.shopease.common.entity.Brand;
import com.shopease.common.entity.Product;




@Controller
public class ProductController {

	@Autowired
	ProductService productService;
	
	@Autowired
	BrandService brandService;
	
	@GetMapping("/products")
	public String listAll(Model model) {
		
		List<Product> listProducts = productService.listAll();
		
		model.addAttribute("listProducts", listProducts);
		
		
		return "products/products";
	}
	
	
	@GetMapping("/products/new")
	public String newProduct(Model model) {
		
		List<Brand> listBrands = brandService.listAll();
		
		Product product = new Product();
		product.setEnabled(true);
		product.setInStock(true);
		
		model.addAttribute("product", product);
		model.addAttribute("listBrands", listBrands);
		model.addAttribute("pageTitle", "Create New Product");
		
		return "products/product_form";
	}
	
	@PostMapping("/products/save")
	public String saveProduct(Product product, RedirectAttributes ra,
			@RequestParam("fileImage") MultipartFile mainImageMultipartFile,
			@RequestParam("extraImage") MultipartFile[] extraImageMultipartFile) throws IOException {
		
		setMainImageName(mainImageMultipartFile, product);
		setExtraImageNames(extraImageMultipartFile, product);
			
		Product savedProduct = productService.save(product);
		
		saveUploadedImages(mainImageMultipartFile, extraImageMultipartFile, savedProduct);
				
		ra.addFlashAttribute("message", "The product has been saved successfully.");
		
		return "redirect:/products";
	}
	

	private void saveUploadedImages(MultipartFile mainImageMultipartFile, MultipartFile[] extraImageMultipartFile,
			Product savedProduct) throws IOException {
		if (!mainImageMultipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(mainImageMultipartFile.getOriginalFilename());
			String uploadDir = "../product-images/" + savedProduct.getId();
			
			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, mainImageMultipartFile);	
		} 
		
		if (extraImageMultipartFile.length > 0) {
			for (MultipartFile multipartFile : extraImageMultipartFile) {
				String uploadDir = "../product-images/" + savedProduct.getId() + "/extras";
				
				if (!multipartFile.isEmpty()) {
					String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
		
					FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);	
				}	
			}
		}
	}


	private void setMainImageName(MultipartFile mainImageMultipartFile, Product product) {
		if (!mainImageMultipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(mainImageMultipartFile.getOriginalFilename());
			product.setMainImage(fileName);
		} 
	}
	
	private void setExtraImageNames(MultipartFile[] extraImageMultipartFile, Product product) {
		if (extraImageMultipartFile.length > 0) {
			for (MultipartFile multipartFile : extraImageMultipartFile) {
				if (!multipartFile.isEmpty()) {
					String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
					product.addExtraImage(fileName);
				}	
			}
		}
	}


	@GetMapping("/products/{id}/enabled/{status}")
	public String updateProductEnabledStatus(@PathVariable("id") Integer id,
			@PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes) {
		productService.updateProductEnabledStatus(id, enabled);
		String status = enabled ? "enabled" : "disabled";
		String message = "The product ID " + id + " has been " + status;
		redirectAttributes.addFlashAttribute("message", message);
		
		return "redirect:/products";
	}
	
	@GetMapping("/products/delete/{id}")
	public String deleteProduct(@PathVariable(name = "id") Integer id, 
			Model model,
			RedirectAttributes redirectAttributes) {
		try {
			productService.delete(id);
			String productImagesDir = "../products-images/" + id + "/extras";
			String productExtraImagesDir = "../products-images/" + id + "/extras";
			FileUploadUtil.removeDir(productImagesDir);
			FileUploadUtil.removeDir(productExtraImagesDir);
			
			redirectAttributes.addFlashAttribute("message", 
					"The product ID " + id + " has been deleted successfully");
		} catch (ProductNotFoundException ex) {
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
		}
		
		return "redirect:/products";
	}
	
}
