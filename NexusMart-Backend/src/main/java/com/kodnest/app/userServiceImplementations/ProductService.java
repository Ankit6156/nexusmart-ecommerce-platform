package com.kodnest.app.userServiceImplementations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.kodnest.app.entities.Category;
import com.kodnest.app.entities.Product;
import com.kodnest.app.entities.ProductImage;
import com.kodnest.app.repositories.CategoryRepository;
import com.kodnest.app.repositories.ProductImageRepository;
import com.kodnest.app.repositories.ProductRepository;
import com.kodnest.app.userServices.ProductServiceContract;

@Service
public class ProductService implements ProductServiceContract{
	
	    private ProductRepository productRepository;
	    private ProductImageRepository productimageRepository;
	    private CategoryRepository categoryRepository;
	    
	   
	public ProductService(ProductRepository productRepository, ProductImageRepository productimageRepository,
				CategoryRepository categoryRepository) {
			super();
			this.productRepository = productRepository;
			this.productimageRepository = productimageRepository;
			this.categoryRepository = categoryRepository;
		}

	@Override
	public List<Product> getProductsByCategory(String categoryName) {
		 if (categoryName != null && !categoryName.isEmpty()) {

	            Optional<Category> categoryOpt =categoryRepository.findByCategoryName(categoryName);

	            if (categoryOpt.isPresent()) {
	                Category category = categoryOpt.get();
	                return productRepository .findByCategory_CategoryId(category.getCategoryId());
	            } else {
	                throw new RuntimeException("Category not found");
	            }
	        } else {
	            return productRepository.findAll();
	        }
	}

	@Override
	public List<String> getProductimages(Integer productId) {
		 List<ProductImage> productimages =
	                productimageRepository.findByProduct_ProductId(productId);

	        List<String> imageUrls = new ArrayList<>();

	        for (ProductImage image : productimages) {
	            imageUrls.add(image.getImageUrl());
	        }
	        return imageUrls;
	}
	
}
