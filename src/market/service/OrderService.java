package market.service;

public interface OrderService {
	void createOrder();
	
	void getOrders();
	
	void getOrderDetails(int orderId);
	
	void updateOrder(String table, String field);
	
	void deleteOrder();
}
