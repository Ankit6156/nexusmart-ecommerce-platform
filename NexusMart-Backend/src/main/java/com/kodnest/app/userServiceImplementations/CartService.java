package com.kodnest.app.userServiceImplementations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.kodnest.app.entities.CartItem;
import com.kodnest.app.entities.Product;
import com.kodnest.app.entities.ProductImage;
import com.kodnest.app.entities.User;
import com.kodnest.app.repositories.CartRepository;
import com.kodnest.app.repositories.ProductImageRepository;
import com.kodnest.app.repositories.ProductRepository;
import com.kodnest.app.repositories.UserRepository;
import com.kodnest.app.userServices.CartServiceContract;

@Service
public class CartService implements CartServiceContract {
	
	private CartRepository cartRepository;
	private ProductRepository productRepository;
	private ProductImageRepository productimageRepository;
	private UserRepository userRepository;
	
	public CartService(CartRepository cartRepository, ProductRepository productRepository,
			ProductImageRepository productimageRepository, UserRepository userRepository) {
		super();
		this.cartRepository = cartRepository;
		this.productRepository = productRepository;
		this.productimageRepository = productimageRepository;
		this.userRepository = userRepository;
	}

	@Override
	public void addToCart(User user, int productId, int quantity) {
		Product product = productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));
				
		// Fetch cart item for this userld and productid
				Optional<CartItem> existingItem = cartRepository.findByUserAndProduct (user.getUserId(), productId);
				if (existingItem.isPresent()) {
				CartItem cartItem = existingItem.get();
				cartItem.setQuantity (cartItem.getQuantity() + quantity);
				cartRepository.save(cartItem);
				} 
				else {
				CartItem newItem = new CartItem (user, product, quantity);
				cartRepository.save(newItem);
				}
	}

	@Override
	public Map<String, Object> getCartItems(User authenticatedUser) {
		
		// Fetch the cart items for the user with product details
	    List<CartItem> cartitems = cartRepository.findCartItemsWithProductDetails(authenticatedUser.getUserId());

	    // Create a response map to hold the cart details
	    Map<String, Object> response = new HashMap<>();

	    response.put("username", authenticatedUser.getUsername());
	    response.put("role", authenticatedUser.getRole().toString());

	    // List to hold the product details
	    List<Map<String, Object>> products = new ArrayList<>();
	    double overallTotalPrice = 0;

	    for (CartItem cartitem : cartitems) {

	        Map<String, Object> productDetails = new HashMap<>();
	        // Get product details
	        Product product = cartitem.getProduct();
	        // Fetch product images
	        List<ProductImage> productimages = productimageRepository.findByProduct_ProductId(product.getProductId());
	       
	        String imageUrl;
	         if(productimages != null && !productimages.isEmpty()) {
	        	 imageUrl = productimages.get(0).getImageUrl();
	        	 } 
	         else {
	        		 imageUrl =  "default-image-url";
	        	 }
	                        
	        // Populate product details
	        productDetails.put("product_id", product.getProductId());
	        productDetails.put("image_url", imageUrl);
	        productDetails.put("name", product.getName());
	        productDetails.put("description", product.getDescription());
	        productDetails.put("price_per_unit", product.getPrice());
	        productDetails.put("quantity", cartitem.getQuantity());
	        productDetails.put("total_price", cartitem.getQuantity() * product.getPrice().doubleValue());
	        
	        // add to ProductList
	        products.add(productDetails);
	        // Update overall total price
	        overallTotalPrice += cartitem.getQuantity() * product.getPrice().doubleValue();
	    }

	    // Prepare the final cart response
	    Map<String, Object> cart = new HashMap<>();
	    cart.put("products", products);
	    cart.put("overall_total_price", overallTotalPrice);
	    response.put("cart", cart);

	    return response;
	}

	@Override
	public void updateCartItemQuantity(User authenticatedUser, int productId, int quantity) {
		User user = userRepository.findById(authenticatedUser.getUserId()).orElseThrow(() -> new IllegalArgumentException("User not found"));
		Product product = productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("Product not found"));
				// Fetch cart item for this userld and productid
				Optional<CartItem> existingItem = cartRepository.findByUserAndProduct (authenticatedUser.getUserId() , productId);
				if (existingItem.isPresent()) {
				CartItem cartitem = existingItem.get();
				if (quantity == 0) {
				deleteCartItem (authenticatedUser.getUserId(), productId);
				} else {
				cartitem.setQuantity (quantity);
				cartRepository.save(cartitem);
				  }
			} else {
				 throw new RuntimeException("Cart item not found associated with product and user");
			}
	}

	@Override
	public void deleteCartItem(int userId, int productId) {
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new IllegalArgumentException("Product not found"));
				cartRepository.deleteCartItem(userId, productId);
		
	}

	@Override
	public int getCartItemCount(int userId) {
		int count = cartRepository.countTotalItems(userId);
		return count;
	}

	
}
