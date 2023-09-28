package com.inn.cafe.restImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.inn.cafe.contents.CafeConstants;
import com.inn.cafe.model.Category;
import com.inn.cafe.rest.CategoryRest;
import com.inn.cafe.service.CategoryService;
import com.inn.cafe.utils.CafeUtils;

@RestController
public class CategoryRestImpl implements CategoryRest{

	@Autowired
	CategoryService categoryService;
	
	@Override
	public ResponseEntity<String> addNewCategory(Map<String, String> RequestMap) {
		try {
			return categoryService.addNewCategory(RequestMap);
		   }catch (Exception e) {
			e.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG+"fghj", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	
	@Override
	public ResponseEntity<List<Category>> getAllCategory(String filterValue) {
		try {
			return categoryService.getAllCategory (filterValue);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);

	}


	@Override
	public ResponseEntity<String> updateCategory(Map<String, String> RequestMap) {
		try {
			return categoryService.updateCategory(RequestMap);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG+"fghj", HttpStatus.INTERNAL_SERVER_ERROR);

	}

}
