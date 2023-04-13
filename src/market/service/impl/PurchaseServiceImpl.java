package market.service.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Scanner;

import market.MarketDatabase;
import market.model.Product;
import market.model.Purchase;
import market.model.Supplier;
import market.service.ProductService;
import market.service.PurchaseService;

public class PurchaseServiceImpl implements PurchaseService {

	private MarketDatabase database;
	private Connection connect;
	private PreparedStatement statement;
	private Scanner in;
	private ResultSet resultSet;
	private LocalDate date = LocalDate.now();
	private HashMap<Integer, Product> product;

	
	private ProductService productService;
	
	public PurchaseServiceImpl(MarketDatabase database, Scanner in, 
			HashMap<Integer, Product> product, ProductService productService) {
		this.database = database;
		this.in = in;
		this.product = product;
		this.productService = productService;
	}

	private void connect() {
		this.connect = database.connect();
	}

	@Override
	public void createPurchase() {
		Stop: {
			connect();

			try {

				int purchaseID;
				do {
					System.out.println("Please enter Purchase ID");
					purchaseID = 0;

					if (connect.isClosed()) {
                    	connect();
                    }
					
					while (true) {
						try {
							purchaseID = in.nextInt();
							if (purchaseID < 0) {
								throw new InputMismatchException();
							}
							in.nextLine();
							break;
						} catch (InputMismatchException | NumberFormatException ex) {
							in.nextLine();
							System.out.println("Invalid Input Please Enter a Valid Value");
						}
					}
					
					statement = connect.prepareStatement("select *from purchases where id = ?");
					statement.setInt(1, purchaseID);
					resultSet = statement.executeQuery();
					if (resultSet.next()) {
						System.out.println("This Purchase ID is Already Exists ...");
						if (purchaseIsExist()) {
							break Stop;
						}
					} else {
						break;
					}
				} while (true);

				int productID = 0;
				do {
					
					if (connect.isClosed()) {
                    	connect();
                    }
					
					System.out.println("Please Enter Product ID");

					while (true) {
						try {
							productID = in.nextInt();
							in.nextLine();
							break;
						} catch (InputMismatchException | NumberFormatException ex) {
							in.nextLine();
							System.out.println("Invalid Input Please Enter a Valid Value");
						}
					}
					if (product.get(productID) == null) {
						System.out.println("This Product is Not Avilable in the Inventory");

						if (productIDIsExist()) {
							break Stop;
						}
					}

				} while (product.get(productID) == null);

				System.out.println("Please Enter the Quantity You Want To Purchase");
				int quantity = 0;

				while (true) {
					try {
						quantity = in.nextInt();
						if (quantity < 0) {
							throw new InputMismatchException();
						}
						break;
					} catch (InputMismatchException | NumberFormatException ex) {
						in.nextLine();
						System.out.println("Invalid Input Please Enter a Valid Value");
					}

				}
				Product temp = new Product(product.get(productID).getProductID(),
						product.get(productID).getProduct_Name(), product.get(productID).getPurchasinPrice(),
						product.get(productID).getSellingPrice(), product.get(productID).getSupplierID(),
						product.get(productID).getQuantity());
				temp.setQuantity(quantity);
				this.product.get(productID).setQuantity(quantity + this.product.get(productID).getQuantity());
				Purchase purchase = new Purchase(purchaseID, date, temp, quantity);
				System.out.println(temp.getSupplierID());

				String updateInventoryQuery = 
						"update inventory set product_quantity = Product_Quantity + ? where product_id =?";

				String insertQuery = "insert into purchases values(?,?,?,?,?,?,?,?)";
				statement = connect.prepareStatement(insertQuery);

				statement.setInt(1, purchase.getId());
				statement.setString(2, purchase.getDate().toString());
				statement.setInt(3, purchase.getProduct().getProductID());
				statement.setString(4, purchase.getProduct().getProduct_Name());
				statement.setFloat(5, purchase.getProduct().getPurchasinPrice());
				statement.setInt(6, purchase.getProduct().getSupplierID());
				statement.setInt(7, purchase.getQuantity());
				statement.setFloat(8, purchase.getQuantity() * purchase.getProduct().getPurchasinPrice());
				statement.execute();
				statement = connect.prepareStatement(updateInventoryQuery);
				statement.setInt(1, purchase.getQuantity());
				statement.setInt(2, purchase.getProduct().getProductID());
				statement.execute();

				System.out.println();

				System.out.println("The Purchasing Process Has Completed Successfully");

				System.out.println();

			} catch (SQLException ex) {
				System.out.println(ex.getMessage());
			} finally {
				disconnect();
			}
		}

	}

	@Override
	public void getPurchases() {
		connect();
        ArrayList<Purchase> purchaseList = new ArrayList<>();
        ArrayList<Supplier> supplierList = new ArrayList<>();
        ArrayList<Float> totalPrice = new ArrayList<>();
        float totalPurchases = 0;
        int counter = 0;
        String query;
        
        query = """
                select purchases.id, purchases.date ,suppliers.id, suppliers.name ,purchases.product_name ,purchases.price,
                purchases.quantity,purchases.total_price from
                purchases left join suppliers on purchases.supplier_id = suppliers.id
                 """;

        try {
            statement = connect.prepareStatement(query);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                purchaseList.add(new Purchase(resultSet.getInt("purchases.id"), resultSet.getDate("purchases.date").toLocalDate(),
                        resultSet.getString("purchases.product_name"), resultSet.getFloat("purchases.price"), resultSet.getInt("purchases.quantity")));
                totalPrice.add(resultSet.getFloat("purchases.total_price"));
                supplierList.add(new Supplier(resultSet.getInt("suppliers.id"), resultSet.getString("suppliers.name")));
            }

            System.out.print("Purchase ID");
            System.out.print("   ");

            System.out.print("Purchase Date");
            System.out.print("      ");

            System.out.print("Supplier ID");
            System.out.print("       ");

            System.out.print("Supplier Name");
            System.out.print("          ");

            System.out.print("Product Name");
            System.out.print("      ");

            System.out.print("Purchasing Price");
            System.out.print("    ");

            System.out.print("Quantity");
            System.out.print("     ");

            System.out.print("Purchase Total Price");
            System.out.println();
            System.out.println();
            while (counter < purchaseList.size()) {
                System.out.print("|");
                System.out.print(purchaseList.get(counter).getId());
                System.out.print("|");
                for (int r = 12 - String.valueOf(purchaseList.get(counter).getId()).length(); r > 0; r--) {
                    System.out.print(" ");

                }
                System.out.print("| ");
                System.out.print(purchaseList.get(counter).getDate());
                for (int r = 16 - purchaseList.get(counter).getDate().toString().length(); r >= 0; r--) {
                    System.out.print(" ");
                }
                System.out.print("|");

                System.out.print(supplierList.get(counter).getId());
                System.out.print("|");
                for (int r = 16 - String.valueOf(supplierList.get(counter).getId()).length(); r > 0; r--) {
                    System.out.print(" ");

                }
                System.out.print("| ");

                System.out.print(supplierList.get(counter).getName());
                for (int r = 20 - supplierList.get(counter).getName().length(); r >= 0; r--) {
                    System.out.print(" ");
                }
                System.out.print("| ");

                System.out.print(purchaseList.get(counter).getProductName());
                for (int r = 15 - String.valueOf(purchaseList.get(counter).getProductName()).length(); r >= 0; r--) {
                    System.out.print(" ");
                }
                System.out.print("| ");

                System.out.print(purchaseList.get(counter).getPrice());
                for (int r = 17 - String.valueOf(purchaseList.get(counter).getPrice()).length(); r >= 0; r--) {
                    System.out.print(" ");
                }
                System.out.print("| ");

                System.out.print(purchaseList.get(counter).getQuantity());
                for (int r = 10 - String.valueOf(purchaseList.get(counter).getQuantity()).length(); r >= 0; r--) {
                    System.out.print(" ");
                }
                System.out.print("| ");

                System.out.print(totalPrice.get(counter));

                System.out.println();
                totalPurchases += totalPrice.get(counter);
                counter++;
            }

            System.out.println("------------------------------------------------------------------------------------------"
                    + "------------------------------------------------------");
            System.out.println("TOTAl PURCHASES                                 "
                    + "                                                                             | " + totalPurchases);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        } finally {
            purchaseList.clear();
            supplierList.clear();
            totalPrice.clear();
            disconnect();
        }

	}

	@Override
	public void updatePurchase(String field) {
		connect();
        char answer;
        StopUpdate:
        {
            CallableStatement callStat;
            int ID = 0;
            getPurchases();

            System.out.println();

            while (true) {
            	
                System.out.println("Enter The Purchase ID You Want To Modify Its Data");

                while (true) {
                    try {
                        ID = in.nextInt();

                        if (ID < 0) {
                            throw new InputMismatchException();
                        }
                        break;
                    } catch (InputMismatchException ex) {
                        System.out.println("Invalid Input Please Enter a Valid Value");
                    }
                }
                try {
                	
                	if (connect.isClosed()) {
                    	connect();
                    }
                	
                    statement = connect.prepareStatement("select *from purchases where id = ?");
                    statement.setInt(1, ID);
                    resultSet = statement.executeQuery();
                    if (!resultSet.next()) {
                        if (purchaseIsNotExist()) {
                            break StopUpdate;
                        }
                    } else {
                        break;
                    }

                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
///////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////

            if (field.equals("date")) {

                try {
                    statement = connect.prepareStatement("select date from purchases where id = ?");
                    statement.setInt(1, ID);
                    resultSet = statement.executeQuery();
                    resultSet.next();
                    String currentDate = resultSet.getString("date");
                    System.out.println("The Current Date for this Purchase is (" + currentDate + ")");

                    String newDate;
                    System.out.println("Please Enter the new Date In This Format (Year / Month / Day)");
                    newDate = in.next();
                    statement = connect.prepareStatement("update purchases set date = ? where id = ?");
                    statement.setString(1, newDate);
                    statement.setInt(2, ID);
                    statement.execute();

                    System.out.println("The Date Has Changed from (" + currentDate + ") to (" + newDate + ")");
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }

            }
///////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////

            if (field.equals("product_id")) {
                int newProductID;
                int oldProductID = 0;
                int purchasedQuantity = 0;
                int inventoryQuantity = 0;
                String currentProductName = "";
                String newProductName;
                try {
                    statement = connect.prepareStatement("select purchases.product_id, purchases.quantity, inventory.product_quantity,"
                            + "inventory.product_name from purchases, inventory where purchases.id = ? "
                            + "and inventory.product_id = purchases.product_id");
                    statement.setInt(1, ID);
                    resultSet = statement.executeQuery();
                    resultSet.next();
                    oldProductID = resultSet.getInt("purchases.product_id");
                    purchasedQuantity = resultSet.getInt("purchases.quantity");
                    inventoryQuantity = resultSet.getInt("inventory.product_quantity");
                    currentProductName = resultSet.getString("inventory.product_name");
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }

                if (purchasedQuantity > inventoryQuantity) {
                    System.out.println("WARNING ****\n"
                            + "Cannot Change the Current Product ((" + currentProductName + ")) Becuase The Quantity of the"
                            + " Product ((" + currentProductName + "))\n"
                            + "in the Inventory now Is Less Than the Purchased Quantity **********");
                } else {
                    System.out.println("The Current Product for This Purchase is (" + currentProductName + ") With ID (" + oldProductID + ")");
                    System.out.println("""
                                       Press (C) to Continue
                                       Press (E) to Back to the Main Menu""");
                    answer = in.next().toUpperCase().charAt(0);
                    while (answer != 'C' && answer != 'E') {
                        System.out.println("""
                                           Invalid Answer ... 
                                           Press (C) to Continue
                                           Press (E) to Back to the Main Menu
                                           """);
                        answer = in.next().toUpperCase().charAt(0);
                    }
                    if (answer == 'E') {
                        break StopUpdate;
                    }
                    while (true) {
                        System.out.println("Please Enter The ID of The New Product");
                        while (true) {

                            try {
                                newProductID = in.nextInt();
                                in.nextLine();
                                break;
                            } catch (InputMismatchException ex) {
                                in.nextLine();
                                System.out.println("Invalid Input Please Enter a Valid Value");
                            }
                        }

                        try {
                        	
                        	if (connect.isClosed()) {
                            	connect();
                            }
                        	
                            statement = connect.prepareStatement("select *from inventory where product_id = ?");
                            statement.setInt(1, newProductID);
                            resultSet = statement.executeQuery();
                            if (!resultSet.next()) {
                                throw new SQLException();
                            }
                            newProductName = resultSet.getString("product_name");

                            break;
                        } catch (SQLException ex) {

                            System.out.println("This Product Does Not Exist");
                            while (true) {
                                System.out.println("""
                                       Press (A) to Enter another Product ID
                                       Press (S) to Show Inventory
                                       Press (E) to Back to the Update Menu
                                       """);
                                answer = in.next().toUpperCase().charAt(0);
                                while (answer != 'A' && answer != 'E' && answer != 'S') {
                                    System.out.println(""" 
                                           Invalid Input
                                           Press (A) to Enter another Product ID
                                           Press (S) to Show Inventory
                                           Press (E) to Back to the Update Menu
                                           """);
                                    answer = in.next().toUpperCase().charAt(0);
                                }
                                if (answer == 'S') {
                                    productService.getProducts();
                                }
                                if (answer == 'A') {
                                    break;
                                }
                                if (answer == 'E') {
                                    break StopUpdate;
                                }
                            }

                        }
                    }

                    try {

                        callStat = connect.prepareCall("call update_purchase_product_id(?,?,?,?)");

                        callStat.setInt(1, ID);
                        callStat.setInt(2, oldProductID);
                        callStat.setInt(3, newProductID);
                        callStat.setInt(4, purchasedQuantity);
                        callStat.execute();

                        System.out.println("Purchase Product Has Changed from (" + currentProductName + ") to (" + newProductName + ")");
                    } catch (SQLException ex) {

                        System.out.println(ex.getMessage());
                    }
                }
            }

            ///////////////////////////////////////////////////////////////////////////////////////////
            /////////////////////////////////////////////////////////////////////////////////////////
            if (field.equals("price")) {
                float currentPrice;
                try {
                    statement = connect.prepareStatement("select price, product_name from purchases where id = ?");
                    statement.setInt(1, ID);
                    resultSet = statement.executeQuery();
                    resultSet.next();
                    currentPrice = resultSet.getFloat("price");
                    String productName = resultSet.getString("product_name");
                    System.out.println("This purchase Has the Product (" + productName + ") with the Current Price (" + currentPrice + ")");

                    Float newPrice;
                    System.out.println("Please Enter the new price");
                    while (true) {
                        try {
                            newPrice = in.nextFloat();
                            if (newPrice < 0) {
                                throw new InputMismatchException();
                            }
                            break;
                        } catch (InputMismatchException ex) {
                            in.next();
                            System.out.println("Invalid Value Please Enter a Valid Value");
                        }
                    }
                    callStat = connect.prepareCall("call update_purchase_price(?,?)");
                    callStat.setInt(1, ID);
                    callStat.setFloat(2, newPrice);
                    callStat.execute();

                    System.out.println("The Price Has Changed from (" + currentPrice + ") to (" + newPrice + ")");
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
/////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////

            if (field.equals("quantity")) {
                int newQuantity;
                int currentQuantity = 0;
                int inventoryQuantity = 0;
                String productName = "";
                try {
                    statement = connect.prepareStatement("select purchases.product_id, purchases.quantity, inventory.product_quantity,"
                            + "inventory.product_name from purchases, inventory where purchases.id = ? "
                            + "and inventory.product_id = purchases.product_id");
                    statement.setInt(1, ID);
                    resultSet = statement.executeQuery();
                    resultSet.next();
                    currentQuantity = resultSet.getInt("purchases.quantity");
                    inventoryQuantity = resultSet.getInt("inventory.product_quantity");
                    productName = resultSet.getString("inventory.product_name");
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }

                System.out.println("The Current Quantity for the Product (" + productName + ") in This Purchase is (" + currentQuantity + ")");
                System.out.println("""
                                       Press (C) to Continue
                                       Press (E) to Back to the Main Menu""");
                answer = in.next().toUpperCase().charAt(0);
                while (answer != 'C' && answer != 'E') {
                    System.out.println("""
                                           Invalid Answer ... 
                                           Press (C) to Continue
                                           Press (E) to Back to the Main Menu
                                           """);
                    answer = in.next().toUpperCase().charAt(0);
                }
                if (answer == 'E') {
                    break StopUpdate;
                }

                System.out.println("Please Enter The New Quantity");
                while (true) {
                    try {
                        newQuantity = in.nextInt();
                        if (newQuantity < 0) {
                            throw new InputMismatchException();
                        }
                        break;
                    } catch (InputMismatchException ex) {
                        in.next();
                        System.out.println("Invalid Value Please Enter a Valid Value");
                    }

                }
                int updatedQuantity = inventoryQuantity + (newQuantity - currentQuantity);
                if (updatedQuantity < 0) {
                    System.out.println("WARNING ****\n"
                            + "Cannot Change the Current Quantity in this Purchase "
                            + "for the Product ((" + productName + ")) Becuase The Quantity of the"
                            + "\nProduct ((" + productName + ")) in the Inventory Will Be (" + updatedQuantity + ")\n"
                            + "So With the New Updated Quantity The Quantity for This Product In The Inventory Will Be Less Than (0)**********");
                } else {

                    try {

                        callStat = connect.prepareCall("call update_purchase_quantity(?,?)");

                        callStat.setInt(1, ID);
                        callStat.setInt(2, newQuantity);

                        callStat.execute();

                        System.out.println("Purchase Quantity Has Changed from (" + currentQuantity + ") to (" + newQuantity + ")");
                    } catch (SQLException ex) {

                        System.out.println(ex.getMessage());
                    }
                }
            }
        }
        disconnect();

	}

	@Override
	public void deletePurchase() {
		StopPurchaseDelete:
        {
            connect();
            char answer;
            int purchaseID;
            int purchaseQuantity;
            int purchaseProductID;
            String purchaseProductName;
            while (true) {
                System.out.println("Enter The ID or The Purchase You Want to Delete");

                while (true) {
                    try {
                        purchaseID = in.nextInt();
                        in.nextLine();
                        if (purchaseID < 0) {
                            throw new InputMismatchException();
                        }
                        break;
                    } catch (InputMismatchException ex) {
                        in.nextLine();
                        System.out.println("Invalid Input Please Enter a Valid Value");
                    }
                }

                try {
                	
                	if (connect.isClosed()) {
                    	connect();
                    }
                	
                    statement = connect.prepareStatement("select * from purchases where id = ?");
                    statement.setInt(1, purchaseID);
                    resultSet = statement.executeQuery();
                    if (!resultSet.next()) {
                        if (purchaseIsNotExist()) {
                            break StopPurchaseDelete;
                        }
                    } else {
                        purchaseProductID = resultSet.getInt("product_id");
                        purchaseQuantity = resultSet.getInt("quantity");
                        purchaseProductName = resultSet.getString("product_name");

                        statement = connect.prepareStatement("select product_quantity from inventory where product_id = ?");
                        statement.setInt(1, purchaseProductID);
                        resultSet = statement.executeQuery();
                        resultSet.next();
                        int inventoryProductQuantity = resultSet.getInt("product_quantity");

                        if (purchaseQuantity > inventoryProductQuantity) {
                            System.out.println("WARNING ****\n"
                                    + "Cannot Delete this Purchase Becuase The Quantity of the"
                                    + " Purchased Product (" + purchaseProductName + ")\n"
                                    + "in the Inventory now which is (" + inventoryProductQuantity + ") Is Less Than the Purchased Quantity"
                                    + " which was (" + purchaseQuantity + ") \n**********\n");

                            System.out.println("""
                                                   Press (A) to Enter Another Purchase ID
                                                   Press (E) to Back to The Main Menu
                                                   """);
                            answer = in.next().toUpperCase().charAt(0);

                            while (answer != 'A' && answer != 'E') {
                                System.out.println("""
                                                       Invalid Answer....
                                                       Press (A) to Enter Another Purchase ID
                                                       Press (E) to Back to The Main Menu
                                                       """);
                                answer = in.next().toUpperCase().charAt(0);
                            }

                           
                            if (answer == 'E') {
                                break StopPurchaseDelete;
                            }
                        } else {
                            break;
                        }

                    }
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }

            }
            try {

                statement = connect.prepareStatement("update inventory set product_quantity = product_quantity - ? where product_id = ?");
                statement.setInt(1, purchaseQuantity);
                statement.setInt(2, purchaseProductID);
                statement.execute();
                statement = connect.prepareStatement("delete from purchases where id = ?");
                statement.setInt(1, purchaseID);
                statement.execute();
                System.out.println("The Purchase Has Deleted Successfully");
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
        disconnect();

	}

	public boolean purchaseIsExist() {
        boolean checker = false;
        char answer;
        Stop:
        {
            System.out.println("");
            while (true) {
                System.out.println("""
                                   Press (A) to Enter Another ID
                                   Press (S) to Show The Applied Purchases
                                   Press (E) to Back to The Main Menu
                                   """);
                
                answer = in.next().toUpperCase().charAt(0);

                if (answer != 'E' && answer != 'A' && answer != 'S') {

                    try {
                        throw new InputMismatchException();

                    } catch (InputMismatchException ex) {
                        System.out.println("Invalid Input Please Enter a Valid Value");
                    }

                }
                if (answer == 'A') {
                    break;
                }
                if (answer == 'S') {
                    getPurchases();
                }
                if (answer == 'E') {
                    checker = true;
                    break Stop;
                }
            }
        }

        return checker;
    }
	
	public boolean purchaseIsNotExist() {
        boolean checker;
        System.out.println("This Purchase ID is Not Exists ...");
        checker = purchaseIsExist();
        return checker;
    }
	
	public boolean productIDIsExist() {
        char answer;
        boolean checker = false;
        Stop:
        {
            while (true) {
                System.out.println("""
                                   Press (A) to Enter Another ID
                                   Press (S) to show the Products
                                   Press (E) to Back to The Main Menu """);
                answer = in.next().toUpperCase().charAt(0);
                if (answer != 'A' && answer != 'S' && answer != 'E') {
                    System.out.println("Invalid Input ........");
                }
                if (answer == 'A') {
                    break;
                }
                if (answer == 'S') {
                    productService.getProducts();
                }
                if (answer == 'E') {
                    checker = true;
                    break Stop;
                }
            }
        }

        return checker;
    }
	
	private void disconnect() {
		database.disconnect(statement, resultSet, connect);
	}

}
