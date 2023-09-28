package com.inn.cafe.model;

import java.io.Serializable;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NamedQueries;

import lombok.Data;

@NamedQuery(
	    name = "Category.getAllCategory",
	    query = "select c from Category c where c.id in (select p.category from Product p where p.status='true')"
	)


@Data
@DynamicUpdate
@DynamicInsert
@Entity
@Table(name="category")
public class Category implements Serializable{

	private static final long serialVersionUID=1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "name")
	private String name;
	 
}
