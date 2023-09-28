package com.inn.cafe.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.inn.cafe.model.User;
import com.inn.cafe.wrapper.UserWrapper;

import java.util.List;

import javax.transaction.Transactional;


@EnableJpaRepositories
public interface UserDao extends JpaRepository<User, Integer>{

	User findByEmailId(@Param("email") String email );
	
	List<UserWrapper> getAllUser();
	
	List<String> getAllAdmin();
	
	@Transactional
	@Modifying
	Integer updateStatus(@Param("status")String status,@Param("id") Integer id);
	
	User  findByEmail(String email);
}
