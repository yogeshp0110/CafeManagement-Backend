package com.inn.cafe.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;


import com.google.common.base.Strings;
import com.inn.cafe.JWT.CustomerUserDetailsService;
import com.inn.cafe.JWT.JwtFilter;
import com.inn.cafe.JWT.JwtUtils;
import com.inn.cafe.contents.CafeConstants;
import com.inn.cafe.dao.CategoryDao;
import com.inn.cafe.model.Category;
import com.inn.cafe.service.CategoryService;
import com.inn.cafe.utils.CafeUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CategoryServiceIml implements CategoryService{
	
	@Autowired
	CategoryDao categoryDao;

	@Autowired
	AuthenticationManager authenticationManager;
	
	@Autowired
	CustomerUserDetailsService customerUserDetailsService;
	
	@Autowired
	JwtUtils jwtUtils;
	
	@Autowired
	JwtFilter jwtFilter;
	
	@Override
	public ResponseEntity<String> addNewCategory(Map<String, String> requestMap) {
     try {
    	 if(jwtFilter.isAdmin()) {
    		 if(validateCategoryMap(requestMap,false)) {
    			  categoryDao.save(getCategoryFromMap(requestMap,false));
    			  return CafeUtils.getResponseEntity("Category Added Successfully", HttpStatus.OK);
    		 }
    		 
    	 }else{
    		 return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
    			
    	 }
    	 
     }catch (Exception e) {
		e.printStackTrace();
	}
		return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	
	
	
	private boolean validateCategoryMap(Map<String, String> requestMap, boolean validateId) {
		if(requestMap.containsKey("name")) {
			if(requestMap.containsKey("id") && validateId) {
				return true;
			}else if(!validateId) {
				return true;
			}
		}
		return false;
	}
	
	
	private Category getCategoryFromMap(Map<String, String> requMap,Boolean isAdd) {
		Category category=new Category();
		if(isAdd) {
			category.setId(Integer.parseInt(requMap.get("id")));
		}
		category.setName(requMap.get("name"));
		return category;
	}




	@Override
	public ResponseEntity<List<Category>> getAllCategory(String filterValue) {
		try {
			if(!Strings.isNullOrEmpty(filterValue) && filterValue.equalsIgnoreCase("true")) {
				log.info("Inside If");
				return new ResponseEntity<List<Category>>(categoryDao.getAllCategory(),HttpStatus.OK);
			}
			return new ResponseEntity<>(categoryDao.findAll(),HttpStatus.OK);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<List<Category>>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
	}




	@Override
	public ResponseEntity<String> updateCategory(Map<String, String> requestMap) {
	try {
		if(jwtFilter.isAdmin()) {
			if(validateCategoryMap(requestMap, true)) {
				Optional<Category> optional= categoryDao.findById(Integer.parseInt(requestMap.get("id")));
		        if(!optional.isEmpty()) {
		        	categoryDao.save(getCategoryFromMap(requestMap, true));
		         	return CafeUtils.getResponseEntity("Category updated Succeefully", HttpStatus.OK);
		 		   
		        }else {
		        	return CafeUtils.getResponseEntity("Category Id does not exist", HttpStatus.OK);
		        }
			}
			return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
		}else {
			 return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
		    	
		}
	}catch (Exception e) {
		e.printStackTrace();
	}
	return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);

	}
	
	
	

}
