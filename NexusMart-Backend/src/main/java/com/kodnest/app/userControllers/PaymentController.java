package com.kodnest.app.userControllers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kodnest.app.entities.OrderItem;
import com.kodnest.app.entities.User;
import com.kodnest.app.userServices.PaymentServiceContract;
import com.razorpay.RazorpayException;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin (origins = "http://localhost:5173", allowCredentials = "true")
@RequestMapping("/api/payment")
public class PaymentController {
	private PaymentServiceContract paymentService;

	public PaymentController(PaymentServiceContract paymentService) {
		super();
		this.paymentService = paymentService;
	}

	@PostMapping("/create")
	public ResponseEntity<String> createPaymentOrder(@RequestBody Map<String, Object> requestBody,HttpServletRequest request) {
		
	try {
	// Fetch authenticated user
	User user = (User) request.getAttribute("authenticatedUser");
	// Extract totalAmount and cartitems from the request body
	BigDecimal totalAmount = new BigDecimal(requestBody.get("totalAmount").toString());
	List<Map<String, Object>> cartItemsRaw = (List<Map<String, Object>>) requestBody.get("cartItems");
	
	// Convert cartitemsRaw to List<Orderltem> Using Stream
//	List<OrderItem> cartitems = cartitemsRaw.stream().map(item -> {
//	Orderitem orderitem = new Orderltem();
//	orderltem.setProductId ((Integer) item.get("productid"));
//	orderltem.setQuantity ((Integer) item.get("quantity"));
//	BigDecimal pricePerUnit = new BigDecimal (item.get("price").toString());
//	orderltem.setPricePerUnit(pricePerUnit);
//	orderltem.setTotalPrice (pricePerUnit.multiply (BigDecimal.valueOf((Integer) item.get("quantity"))));
//	return orderltem;
//	}).collect(Collectors.toList());
	
	
	// Convert cartitemsRaw to List<Orderltem>
	List<OrderItem> cartItems = new ArrayList<>();
	
	for(Map<String, Object> map : cartItemsRaw) {
		OrderItem orderItem = new OrderItem();
		orderItem.setProductId ((Integer) map.get("productId"));
		orderItem.setQuantity ((Integer) map.get("quantity"));
		BigDecimal pricePerUnit = new BigDecimal (map.get("price").toString());
		orderItem.setPricePerUnit(pricePerUnit);
		orderItem.setTotalPrice (pricePerUnit.multiply (BigDecimal.valueOf((Integer) map.get("quantity"))));
		cartItems.add(orderItem);
	}
	
	// Call the payment service to create a Razorpay order
	String razorpayOrderId = paymentService.createOrder(user.getUserId(), totalAmount, cartItems);
	return ResponseEntity.ok(razorpayOrderId);
	} catch (RazorpayException e) {
	e.printStackTrace();
	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating Razorpay order: " +e.getMessage());
	} 
	catch (Exception e) {
	e.printStackTrace();
	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request data: " + e.getMessage());
	}
	
	}
	
	@PostMapping("/verify")
	public ResponseEntity<String> verifyPayment(@RequestBody Map<String, Object> requestBody, HttpServletRequest request) {
	try {
	// Fetch authenticated user
	User user = (User) request.getAttribute("authenticatedUser");
	if (user == null) {
	return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
	}
	int userId=user.getUserId();
	
	// Extract Razorpay payment details from the request body
	String razorpayOrderId = (String) requestBody.get("razorpayOrderId");
	String razorpayPaymentId = (String) requestBody.get("razorpayPaymentId");
	String razorpaySignature = (String) requestBody.get("razorpaySignature");
	
	// Call the payment service to verify the payment
	boolean isVerified = paymentService.verifyPayment (razorpayOrderId, razorpayPaymentId, razorpaySignature, userId);
	if (isVerified) {
	return ResponseEntity.ok("Payment verified successfully");
	} else {
	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment verification failed");
	}
	} catch (Exception e) {
	e.printStackTrace();
	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error verifying payment: " + e.getMessage());
	}
	}
	
}