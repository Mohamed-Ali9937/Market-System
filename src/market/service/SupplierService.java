package market.service;

public interface SupplierService {

	void addSupplier();
	
	char getBreakChar();
	
	void getSuppliers();
	
	void updateSupplier(String field);
	
	/* Note that there is no need to add deleteSupplier method 
	 * because in real-time application like this suppliers and customers cannot be deleted 
	 * because they hold sensitive data that effect the Income statement and Balance sheet of the firm
	 */

}
