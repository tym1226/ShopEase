package com.shopease.admin.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.shopease.admin.paging.SearchRepository;
import com.shopease.common.entity.User;

public interface UserRepository extends SearchRepository<User, Integer>{
	
	@Query("SELECT user FROM User user WHERE user.email = :email")
	public User getUserByEmail(@Param("email") String email);
	
	public Long countById(Integer id);
	
	@Query("UPDATE User user SET user.enabled = ?2 WHERE user.id = ?1")
	@Modifying
	public void updateEnabledStatus(Integer id, boolean enabled);
	
	@Query("SELECT user FROM User user WHERE CONCAT(user.id, ' ', user.email, ' ', user.firstName, ' ',"
			+ " user.lastName) LIKE %?1%")
	public Page<User> findAll(String keyword, Pageable pageable);
}
