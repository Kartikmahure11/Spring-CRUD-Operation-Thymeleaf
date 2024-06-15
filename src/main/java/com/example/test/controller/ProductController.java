package com.example.test.controller;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.test.model.Product;
import com.example.test.model.ProductDto;
import com.example.test.services.ProductRepository;
import com.sun.xml.bind.api.impl.NameConverter.Standard;

 

 
@Controller
 
public class ProductController 
{
 @Autowired
  private ProductRepository repo;
 
 
 @RequestMapping("/products")
  public String showProductList(Model m)
  {
	  List<Product> products=repo.findAll();
	  m.addAttribute("products",products); 
	  return "products/index";
	  
  }
 
 @RequestMapping("/create")
 public String showCreatePage(Model m)
 {
     ProductDto productDto=new ProductDto();
     m.addAttribute("productDto",productDto);
     return "products/CreateProduct"; 
 }
 
 
 @PostMapping("/create")
 
 public String createProduct(@Valid @ModelAttribute ProductDto  productDto, BindingResult result)
 {
   if(productDto.getImageFile().isEmpty())
   {
	   result.addError(new FieldError("productDto", "imageFile","The Image file is required"));
   }
   
   if(result.hasErrors())
   {
	   return "products/CreateProduct";
   }
   
   //save
   
   MultipartFile image=productDto.getImageFile();
   Date createdAt= new Date();
   String storageFileName=createdAt.getTime() + "_" +image.getOriginalFilename();
   
   try
   {
	   String uploadDir="public/images/";
	   Path uploadPath=Paths.get(uploadDir);
	   
	   if(!Files.exists(uploadPath))
	   {
		   Files.createDirectories(uploadPath);
	   }
	   
	   try(InputStream inputStream=image.getInputStream())
	   {
		   Files.copy(inputStream,Paths.get(uploadDir + storageFileName),StandardCopyOption.REPLACE_EXISTING);
	   }
   }
   catch(Exception e)
   {
	  System.out.println("Exeption :"+ e.getMessage());  
   }
   
   
   Product product=new Product();
   product.setName(productDto.getName());
   product.setBrand(productDto.getBrand());
   product.setCategory(productDto.getCategory());
   product.setPrice(productDto.getPrice());
   product.setDescription(productDto.getDescription());
   product.setCreatedAt(createdAt);
   product.setImageFilename(storageFileName);
   
   repo.save(product);
   
   return "redirect:/products";
     
 }
 
 @RequestMapping("/edit")
 public String showEditPage(Model m,@RequestParam int id)
 {
	 try
	 {
		 Product product=repo.findById(id).get();
		 m.addAttribute("product",product);
		 
		   ProductDto productDto=new ProductDto();
		   product.setName(productDto.getName());
		   product.setBrand(productDto.getBrand());
		   product.setCategory(productDto.getCategory());
		   product.setPrice(productDto.getPrice());
		   product.setDescription(productDto.getDescription());
		   
		   m.addAttribute("productDto",productDto);
		 
	 }
	 
	 catch(Exception e)
	 {
		System.out.println("Exception:"+e.getMessage());
		return "redirect:/products";
	 }
	 
	return "products/EditProduct"; 
 }
 
 
  @RequestMapping("/delete")
  
  public  String deleteProduct(@RequestParam int id)
  {
	 try
	 {
		 Product product = repo.findById(id).get();
		 Path imagePath = Paths.get("public/images/" + product.getImageFilename());
		 
		 try
		 {
			 Files.delete(imagePath);
			 
		 }
		 
		 catch(Exception ex)
		 {
			System.out.println("Exception:"+ex.getMessage()); 
		 }
		 
		 repo.delete(product);
	 }
	 
	 catch(Exception ex)
	 {
		System.out.println("Exception:"+ex.getMessage());
	 }
	 
	 return "redirect:/products";
  }
 
 
	

}
