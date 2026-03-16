package com.kodnest.app.adminServiceImplementation;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kodnest.app.adminServices.AdminProductServiceContract;
import com.kodnest.app.entities.Category;
import com.kodnest.app.entities.Product;
import com.kodnest.app.entities.ProductImage;
import com.kodnest.app.repositories.CartRepository;
import com.kodnest.app.repositories.CategoryRepository;
import com.kodnest.app.repositories.ProductImageRepository;
import com.kodnest.app.repositories.ProductRepository;

@Service
public class AdminProductService implements AdminProductServiceContract{
	
	private final ProductRepository productRepository;
	private final ProductImageRepository productImageRepository;
	private final CategoryRepository categoryRepository;
	private final CartRepository cartRepository;

	public AdminProductService(ProductRepository productRepository, ProductImageRepository productImageRepository,
			CategoryRepository categoryRepository, CartRepository cartRepository) {
		super();
		this.productRepository = productRepository;
		this.productImageRepository = productImageRepository;
		this.categoryRepository = categoryRepository;
		this.cartRepository = cartRepository;
	}

	@Override
	public Product addProductWithImage(String name, String description, Double price, Integer stock, Integer categoryId,
			String imageUrl) {
		// check if cat carteogory exits using categoryId
		Optional<Category> category = categoryRepository.findById(categoryId);
		// if exits create product and add all the values to all  attribute to product
		if(category.isEmpty()) {
			throw new IllegalArgumentException("Invalid category ID");
		}
		
		Product product = new Product(name, description, BigDecimal.valueOf(price), stock, category.get(), LocalDateTime.now(), LocalDateTime.now());
		// save product
		Product savedProduct = productRepository.save(product);
		// check if iamage url is null or balank or empty
		// if image url exits create ProductImage and set values to attirbute and save ProductImage
		if(imageUrl != null && !imageUrl.isEmpty()) {
			ProductImage image = new ProductImage(savedProduct, imageUrl);
			productImageRepository.save(image);
		}
		return savedProduct;
	}

	@Transactional
	@Override
	public void deleteProduct(Integer productId) {

	    if (!productRepository.existsById(productId)) {
	        throw new IllegalArgumentException("Product not found");
	    }
	    // Delete cart items referencing this product
	    cartRepository.deleteByProductId(productId);

	    // Delete product images
	    productImageRepository.deleteByProductId(productId);

	    // Delete the product
	    productRepository.deleteById(productId);
	}

}
