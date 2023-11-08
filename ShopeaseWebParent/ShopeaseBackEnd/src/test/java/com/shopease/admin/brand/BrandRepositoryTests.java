package com.shopease.admin.brand;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;

import com.shopease.common.entity.Brand;
import com.shopease.common.entity.Category;


@DataJpaTest(showSql = true)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class BrandRepositoryTests {
	
	@Autowired
	private BrandRepository repo;
	
	@Test
	public void testCreateBrand1() {
		String defaultImage = "brand-logo.png";
		Set<Category> categories = new HashSet<>();
		Category laptops = new Category(6);
		categories.add(laptops);
		Brand brand = new Brand("Acer", defaultImage, categories);
		Brand savedBrand = repo.save(brand);
		
		assertThat(savedBrand).isNotNull();
		
	}
	
	@Test
	public void testCreateBrand2() {
		String defaultImage = "brand-logo.png";
		Set<Category> categories = new HashSet<>();
		Category tablets = new Category(7);
		Category laptops = new Category(6);
		categories.add(laptops);
		categories.add(tablets);
		Brand brand = new Brand("Apple", defaultImage, categories);
		Brand savedBrand = repo.save(brand);
		
		assertThat(savedBrand).isNotNull();
		
	}
	
	@Test
	public void testFindById() {
		Brand brand = repo.findById(2).get();
		
		assertThat(brand.getName()).isEqualTo("Acer");
	}
	
	@Test
	public void testFindAll() {
		Iterable<Brand> brands = repo.findAll();
		brands.forEach(brand -> System.out.println(brand.getName()));
		
		assertThat(brands).isNotEmpty();
	}
}
