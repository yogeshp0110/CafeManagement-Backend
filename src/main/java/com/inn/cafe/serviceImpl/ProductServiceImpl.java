package com.inn.cafe.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder.In;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.google.common.base.Optional;
import com.inn.cafe.JWT.JwtFilter;
import com.inn.cafe.contents.CafeConstants;
import com.inn.cafe.dao.ProductDao;
import com.inn.cafe.model.Category;
import com.inn.cafe.model.Product;
import com.inn.cafe.service.ProductService;
import com.inn.cafe.utils.CafeUtils;
import com.inn.cafe.wrapper.ProductWrapper;

@Service
public class ProductServiceImpl implements ProductService {
	
	@Autowired
	ProductDao productDao;
	
	@Autowired
	JwtFilter jwtFilter;
	

	@Override
	public ResponseEntity<String> addNewProduct(Map<String, String> requestMap) {
		try {
			
			  if(jwtFilter.isAdmin()) {
				if(ValidateProductMap(requestMap,false)) {
				
					productDao.save(getProductFromMap(requestMap,false));
					return CafeUtils.getResponseEntity("Product Added Succefully.", HttpStatus.OK );
				       
				}
				return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
		        
				
			  }else 
				return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }catch (Exception e) {
			e.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);

	}


	private Product getProductFromMap(Map<String, String> requestMap, boolean isAdd) {
		Category category=new Category();
		category.setId(Integer.parseInt(requestMap.get("categoryId")));
				
		Product product=new Product();
		if(isAdd) {
			product.setId(Integer.parseInt(requestMap.get("id")));
		}else {
			product. setStatus("true");
		}
		product.setCategory(category);
		product.setName(requestMap.get("name"));
		product.setDescription(requestMap.get("description"));
		product.setPrice(Integer.parseInt(requestMap.get("price")));
		return product;
	}


	private boolean ValidateProductMap(Map<String, String> requestMap, boolean validateId) {
		if(requestMap.containsKey("name")){
			if(requestMap.containsKey("id") && validateId) {
			return true;
		}else if(!validateId) {
			return true;
		}
	}
		return false;
   }


	@Override
	public ResponseEntity<List<ProductWrapper>> getAllProduct() {
		try {
			return new ResponseEntity<>(productDao.getAllProduct(),HttpStatus.OK );
		}catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
	}


	@Override
	public ResponseEntity<String> updateProduct(Map<String, String> requestMap) {
		try {
			if(jwtFilter.isAdmin()) {
				if(ValidateProductMap(requestMap, true)) {
					
				java.util.Optional<Product> optional=productDao.findById(Integer.parseInt(requestMap.get("id")));
					if(!optional.isEmpty()) {
						Product product=getProductFromMap(requestMap, true);
						product.setStatus(optional.get().getStatus());
						productDao.save(product);
						return CafeUtils.getResponseEntity("Product Updated Succefully", HttpStatus.OK);
						
					}else {
						return CafeUtils.getResponseEntity("Product ID is not Exixts", HttpStatus.OK);
					}
				}else {
					return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
					   
				}
				  
			}else {
				return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
			      
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);

	}


	@Override
	public ResponseEntity<String> deleteProduct(Integer id) {
		try {
			if(jwtFilter.isAdmin())
			{
				java.util.Optional<Product> optional=productDao.findById(id);
				if(!optional.isEmpty()) {
					
					productDao.deleteById(id);
					return CafeUtils.getResponseEntity("Product deleted Successfully.", HttpStatus.OK);
					
				}
				return CafeUtils.getResponseEntity("Product id does not exist.", HttpStatus.OK);
				
			}else {
				return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}


	@Override
	public ResponseEntity<String> updateStatus(Map<String, String> requestMap) {
		try {
			if(jwtFilter.isAdmin())
			{
				java.util.Optional<Product> optional=productDao.findById(Integer.parseInt("id"));
				if(!optional.isEmpty()) {
					
					productDao.updateProductStatus(requestMap.get("status"),Integer.parseInt("id"));
				
				
					
				}
				return CafeUtils.getResponseEntity("Product id does not exist.", HttpStatus.OK);
				
			}else {
				return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	
}
