package com.kodnest.app.userControllers;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.kodnest.app.entities.User;
import com.kodnest.app.userServices.CartServiceContract;
import java.util.Map;
@RestController
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RequestMapping("/api/cart")
public class CartController {
	
	private CartServiceContract cartService;
      
	  public CartController(CartServiceContract cartService) {
		super();
		this.cartService = cartService;
	}
	  
	  @PostMapping("/add")
	  @CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
	  public ResponseEntity<Void> addToCart(@RequestBody Map<String, Object> request, HttpServletRequest req) {

	      User user = (User) req.getAttribute("authenticatedUser");
	      int productId = (int) request.get("productId");
	      // Handle quantity: Default to 1 if not provided
	      int quantity = request.containsKey("quantity") ? (int) request.get("quantity") : 1;
	      // Add the product to the cart
	      cartService.addToCart(user, productId, quantity);

	      return ResponseEntity.status(HttpStatus.CREATED).build();
	  }
	  
	  // Fetch all cart items for the user (based on username)
      @GetMapping("/items")
      public ResponseEntity<Map<String, Object>> getCartItems(HttpServletRequest request) {

    // Fetch user by username to get the userId
    User user = (User) request.getAttribute("authenticatedUser");
    // Call the service to get cart items for the user
    Map<String, Object> cartitems = cartService.getCartItems(user);

    return ResponseEntity.ok(cartitems);
   }
      
      @PutMapping("/update")
      public ResponseEntity<Void> updateCartItemQuantity(@RequestBody Map<String, Object> request, HttpServletRequest req ) {
      int productId = (int) request.get("productId");
      int quantity = (int) request.get("quantity");
      User user = (User)req.getAttribute("authenticatedUser");
      // Update the cart item quantity
      cartService.updateCartItemQuantity(user, productId, quantity);
      return ResponseEntity.status(HttpStatus.OK).build();
      }
      
      @DeleteMapping("/delete")
      public ResponseEntity<Void> deleteCartItem(@RequestBody Map<String, Object> request, HttpServletRequest req) {
      int productId = (int) request.get("productId");
      User user = (User)req.getAttribute("authenticatedUser");
      // Delete the cart item
      cartService.deleteCartItem(user.getUserId(), productId);
      return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
      }
      
      @GetMapping("/items/count")
      public ResponseEntity<Integer>getCartItemCount(@RequestParam String username, HttpServletRequest request) {
    	  User user = (User)request.getAttribute("authenticatedUser");
    	  int cartCount = cartService.getCartItemCount(user.getUserId());
    	  return ResponseEntity.ok(cartCount);
      }

}
