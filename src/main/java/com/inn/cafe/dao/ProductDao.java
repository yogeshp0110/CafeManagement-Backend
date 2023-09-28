package com.inn.cafe.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.inn.cafe.model.Product;
import com.inn.cafe.wrapper.ProductWrapper;



@Repository
public interface ProductDao extends JpaRepository<Product, Integer> {

   List<ProductWrapper> getAllProduct();
   
   @Modifying
   @Transactional
   Integer updateProductStatus(@Param("status") String status,@Param("id") Integer id);	

}
