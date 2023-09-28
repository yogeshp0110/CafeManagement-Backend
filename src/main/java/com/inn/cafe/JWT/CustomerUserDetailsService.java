package com.inn.cafe.JWT;

import java.util.ArrayList;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.User;
import com.inn.cafe.dao.UserDao;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CustomerUserDetailsService implements UserDetailsService{
	

	@Autowired
	private UserDao userDao;
	
	private  com.inn.cafe.model.User userDatails;
	
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.info("Inside loadUserByUsername{}", username);
		userDatails= userDao.findByEmailId(username);
		if(!Objects.isNull(userDatails))
			 return new User(userDatails.getEmail(), userDatails.getPassword(),new ArrayList<>());
		else
			throw new UsernameNotFoundException("User not found");
	}
	
	
	public com.inn.cafe.model.User getUserDatails(){
		return userDatails;
	}

}
