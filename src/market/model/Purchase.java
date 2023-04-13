/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package market.model;

import java.time.LocalDate;

/**
 *
 * @author Mohamed
 */
public class Purchase {

    private int id;
    private LocalDate date;
    private Product product;
    private String productName;
    private float price;
    private int quantity;

    public Purchase() {
        
    }

    public Purchase(int id,LocalDate date ,Product product, String productName, float price, int quantity) {
        this.id = id;
        this.date = date;
        this.product = product;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
    }
    public Purchase(int id,LocalDate date ,Product product, int quantity) {
        this.id = id;
        this.date = date;
        this.product = product;
        this.quantity = quantity;
    }
    public Purchase(int id,LocalDate date , String productName, float price, int quantity) {
        this.id = id;
        this.date = date;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
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
    
    

    public Product getProduct() {
        return this.product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
}
