package com.example.test.services;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.test.model.Product;


public interface ProductRepository extends JpaRepository<Product,Integer> 
{
	

}
