package market.model;

public class Product {
    private int productID;
    private String product_Name;
    private float purchasinPrice;
    private float SellingPrice;
    private Supplier supplier;
    private int SupplierID;
    private int quantity;

  

    public Product(){
        
    }
    
    public Product(int productID, String product_Name, float purchasinPrice, float SellingPrice, Supplier supplier) {
        this.productID = productID;
        this.product_Name = product_Name;
        this.purchasinPrice = purchasinPrice;
        this.SellingPrice = SellingPrice;
        this.supplier = supplier;
    }

    
    public Product(int productID, String product_Name, float purchasinPrice, float SellingPrice, int supplierID, int quantity) {
        this.productID = productID;
        this.product_Name = product_Name;
        this.purchasinPrice = purchasinPrice;
        this.SellingPrice = SellingPrice;
        this.SupplierID = supplierID;
        this.quantity = quantity;
    }
    
    public Product(int productID, float SellingPrice, int quantity) {
        this.productID = productID;
        this.SellingPrice = SellingPrice;
        this.quantity = quantity;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public String getProduct_Name() {
        return product_Name;
    }

    public void setProduct_Name(String product_Name) {
        this.product_Name = product_Name;
    }

    public float getPurchasinPrice() {
        return purchasinPrice;
    }

    public void setPurchasinPrice(float purchasinPrice) {
        this.purchasinPrice = purchasinPrice;
    }

    public float getSellingPrice() {
        return SellingPrice;
    }

    public void setSellingPrice(float SellingPrice) {
        this.SellingPrice = SellingPrice;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }
   
   public int getSupplierID() {
        return SupplierID;
    }

    public void setSupplierID(int SupplierID) {
        this.SupplierID = SupplierID;
    }
     
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    
}
