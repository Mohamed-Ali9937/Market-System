package market.service;

public interface PurchaseService {
	
	void createPurchase();
	
	void getPurchases();
	
	void updatePurchase(String field);
	
	void deletePurchase();
}
