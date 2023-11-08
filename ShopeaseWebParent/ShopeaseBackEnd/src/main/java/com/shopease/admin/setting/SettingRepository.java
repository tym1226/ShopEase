package com.shopease.admin.setting;

import org.springframework.data.repository.CrudRepository;

import com.shopease.common.entity.Setting;

public interface SettingRepository extends CrudRepository<Setting, String> {
	
	
}
