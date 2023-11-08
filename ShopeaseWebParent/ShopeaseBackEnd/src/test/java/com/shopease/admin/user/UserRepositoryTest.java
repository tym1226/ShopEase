package com.shopease.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest; 
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import com.shopease.common.entity.Role;
import com.shopease.common.entity.User;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UserRepositoryTest {
	
	@Autowired
	private UserRepository repo;
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Test
	public void testCreateUserWithOneRole() {
		Role roleAdmin = entityManager.find(Role.class, 1);
		User userAmy = new User("amy@gmail.com", "1234", "Amy", "Green");
		userAmy.addRole(roleAdmin);
		
		User savedUser = repo.save(userAmy);
		assertThat(savedUser.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testCreateUserWithTwoRoles() {
		User userKen = new User("k@gmail.com", "1234", "Ken", "Kendal");
		Role roleEditor = new Role(1);
		Role roleAssistant = new Role(3);
		userKen.addRole(roleEditor);
		userKen.addRole(roleAssistant);
		
		User savedUser = repo.save(userKen);
		
		assertThat(savedUser.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testListAllUsers() {
		Iterable<User> listUsers = repo.findAll();
		listUsers.forEach(user -> System.out.println(user));
		
	}
	
	@Test
	public void testGetUserById() {
		User userAmy = repo.findById(2).get();
		System.out.println(userAmy);
		assertThat(userAmy).isNotNull();
	}
	
	@Test
	public void testUpdateUserDetails() {
		User userAmy = repo.findById(2).get();
		userAmy.setEnabled(true);
		userAmy.setEmail("amy@hotmail.com");
		
		repo.save(userAmy);
	}
	
	@Test
	public void testUpdateUserRole() {
		User userFrank = repo.findById(3).get();
		Role roleEditor = new Role(3);
		userFrank.getRoles().remove(roleEditor);
		userFrank.addRole(new Role(2));
		
		repo.save(userFrank);
	}
	
	@Test
	public void testDeleteUser() {
		Integer userId = 2;
		repo.deleteById(userId);
	}
	
	@Test
	public void testGetUserByEmail() {
		String email ="panpan@gmail.com";
		User user = repo.getUserByEmail(email);
		assertThat(user).isNotNull();
	}
	
	@Test
	public void testCountById() {
		Integer id = 18;
		Long countById = repo.countById(id);
		assertThat(countById).isNotNull().isGreaterThan(0);
	}
	
	@Test
	public void testDisableUser() {
		Integer id = 17;
		repo.updateEnabledStatus(id, false);
		
		
	}
	
	@Test
	public void testEnableUser() {
		Integer id = 17;
		repo.updateEnabledStatus(id, true);
		
	}
	
	@Test
	public void testListFirstPage() {
		int pageNumber = 1;
		int pageSize = 4;
		Pageable pageable = PageRequest.of(pageNumber, pageSize);
		Page<User> page = repo.findAll(pageable);
		
		List<User> listUsers = page.getContent();
		
		listUsers.forEach(user -> System.out.println(user));
		
		assertThat(listUsers.size()).isEqualTo(pageSize);
	}
	
	@Test
	public void testSearchUsers() {
		String keyword = "ali";
		
		int pageNumber = 0;
		int pageSize = 4;
		Pageable pageable = PageRequest.of(pageNumber, pageSize);
		Page<User> page = repo.findAll(keyword, pageable);
		
		List<User> listUsers = page.getContent();
		
		listUsers.forEach(user -> System.out.println(user));
		
		assertThat(listUsers.size()).isGreaterThan(0);
		
	}

}
