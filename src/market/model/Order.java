package market.model;

import java.util.ArrayList;
import java.time.LocalDate;

public class Order {
    private int id;
    private LocalDate date;
    private Customer customers;
    private ArrayList <Product> products ;
    private int customerID;
    
    private float orderPrice;

    {
    	products = new ArrayList<>();
    }
    
    public Order(){
        
    }
    
    public Order(int id, LocalDate date, Customer cus) {
        this.id = id;
        this.date = date;
        this.customers = cus;
    }
    
    public Order(int id, LocalDate date, float orderPrice) {
        this.id = id;
        this.date = date;
        this.orderPrice = orderPrice;
    }
    
    public ArrayList<Product> getProducts() {
        return products;
    }

    public void setProducts(Product product) {
        this.products.add(product);
    }
    
    public Order(int id, LocalDate date, int customerID) {
        this.id = id;
        this.date = date;
        this.customerID = customerID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Customer getCustomer() {
        return customers;
    }

    public void setCustomer(Customer cus) {
        this.customers = cus;
    }

    public int getCustomerID() {
		return customerID;
	}

	public void setCustomerID(int customerID) {
		this.customerID = customerID;
	}

	public float getOrderPrice() {
        if(this.products != null){
        for(int i = 0; i < products.size(); i++){
            this.orderPrice += (products.get(i).getSellingPrice() * products.get(i).getQuantity());
        }
        }
        return orderPrice;
    }

    public void setOrderPrice(float orderPrice) {
        
        this.orderPrice = orderPrice;
    }
    
}
