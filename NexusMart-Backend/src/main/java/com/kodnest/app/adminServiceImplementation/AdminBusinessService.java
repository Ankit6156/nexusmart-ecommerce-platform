package com.kodnest.app.adminServiceImplementation;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.kodnest.app.adminServices.AdminBusinessServiceContract;
import com.kodnest.app.entities.Order;
import com.kodnest.app.entities.OrderItem;
import com.kodnest.app.entities.Product;
import com.kodnest.app.repositories.OrderItemRepository;
import com.kodnest.app.repositories.OrderRepository;
import com.kodnest.app.repositories.ProductRepository;

@Service
public class AdminBusinessService implements AdminBusinessServiceContract {
	private final OrderRepository orderRepository;
	private final OrderItemRepository orderItemRepository;
	private final ProductRepository productRepository;


	public AdminBusinessService(OrderRepository orderRepository, OrderItemRepository orderItemRepository, ProductRepository productRepository) {
		super();
		this.orderRepository = orderRepository;
		this.orderItemRepository = orderItemRepository;
		this.productRepository = productRepository;
	}
	

	    @Override
	    public Map<String, Object> calculateMonthlyBusiness (int month, int year) {
		List<Order> successfulOrders = orderRepository.findSuccessfulOrdersByMonthAndYear(month, year);
		return calculateBusinessMetrics (successfulOrders);
		}
	    
	    @Override
		public Map<String, Object> calculateDailyBusiness (LocalDate date) {
		List<Order> successfulOrders = orderRepository.findSuccessfulOrdersByDate(date);
		return calculateBusinessMetrics (successfulOrders);
		}
	    
	    @Override
		public Map<String, Object> calculateYearlyBusiness (int year) {
		List<Order> successfulOrders = orderRepository.findSuccessfulOrdersByYear(year);
		return calculateBusinessMetrics (successfulOrders);
		}
	    
	    @Override
		public Map<String, Object> calculateOverallBusiness() {
		List<Order> successfulOrders = orderRepository.findAllByStatusForOverallBusiness();
		 return calculateBusinessMetrics (successfulOrders);
		}
	    private Map<String, Object> calculateBusinessMetrics(List<Order> orders) {

	        double totalRevenue = 0.0;
	        Map<String, Integer> categorySales = new HashMap<>();

	        for (Order order : orders) {

	            totalRevenue += order.getTotalAmount().doubleValue();

	            // Existing repository call
	            List<OrderItem> items =
	                orderItemRepository.findByOrderId(order.getOrderId());

	            for (OrderItem item : items) {

	                // OPTION 2 FIX (Manual Product Fetch)
	                Product product = productRepository.findById(item.getProductId()).orElseThrow(() ->
	                            new RuntimeException("Product not found for ID: "+ item.getProductId()));

	                String categoryName = product.getCategory().getCategoryName();

	                categorySales.put(categoryName, categorySales.getOrDefault(categoryName, 0)+ item.getQuantity());
	            }
	        }

	        Map<String, Object> metrics = new HashMap<>();
	        metrics.put("totalRevenue", totalRevenue);
	        metrics.put("categorySales", categorySales);

	        return metrics;
	    }
	
}
