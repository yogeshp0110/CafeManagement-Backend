package com.inn.cafe.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inn.cafe.model.Category;
@Repository
public interface CategoryDao extends JpaRepository<Category, Integer>{

	List<Category> getAllCategory();
}
