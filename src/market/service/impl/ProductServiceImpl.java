package market.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Scanner;

import market.Utils;
import market.MarketDatabase;
import market.model.Product;
import market.model.Supplier;
import market.service.ProductService;
import market.service.SupplierService;

public class ProductServiceImpl implements ProductService {

	private MarketDatabase database;
	private Connection connect;
	private PreparedStatement statement;
	private Scanner in;
	private ResultSet resultSet;
	private SupplierService supplierService;
	HashMap<Integer, Product> product;
	HashMap<String, Product> productByName;
	HashMap<Integer, Supplier> supplier;
	
	public ProductServiceImpl(MarketDatabase database, Scanner in, 
			SupplierService supplierService, HashMap<Integer, Product> product,
			HashMap<String, Product> productByName, HashMap<Integer, Supplier> supplier) {
		this.database = database;
		this.in = in;
		this.supplierService = supplierService;
		this.product = product;
		this.productByName = productByName;
		this.supplier = supplier;
	}

	private void connect() {
		this.connect = database.connect();
	}

	@Override
	public void addProduct() {

		Stop: {
			connect();
			
			Utils.breakChar = ' ';
			char answer;
			int productID = 0;
			do {
				System.out.println("Please Enter The ID for The New Product");
				
				try {
					if (connect.isClosed()) {
						connect();
					}
				} catch (SQLException ex) {
					System.out.println(ex.getMessage());
				}
				
				while (true) {
					try {
						
						productID = in.nextInt();
						in.nextLine();
						if (productID < 0) {
							throw new InputMismatchException();
						}
						break;
					} catch (InputMismatchException | NumberFormatException ex) {
						in.nextLine();
						System.out.println("Invalid Input Please Enter a Valid Value");
					}
				}

				if (product.get(productID) != null) {

					if (productIDIsExist()) {
						break Stop;
					}
				}

			} while (product.get(productID) != null);

			String productName = "";
			do {
				System.out.println("Enter New Product Name");

				productName = in.nextLine();
				if (productByName.get(productName) != null) {

					if (productNameIsExist()) {
						break Stop;
					}

				}
			} while (productByName.get(productName) != null);

			float purchasingPrice = 0;
			System.out.println("Please Enter Purchasing Price");
			while (true) {

				try {
					purchasingPrice = in.nextFloat();
					if (purchasingPrice < 0) {
						throw new InputMismatchException();
					}
					in.nextLine();
					break;
				} catch (InputMismatchException | NumberFormatException ex) {
					in.nextLine();
					System.out.println("Invalid Input Please Enter a Valid Value");
				}
			}

			float sellingPrice = 0;
			System.out.println("Please Enter Selling Price");
			while (true) {

				try {
					sellingPrice = in.nextFloat();
					if (sellingPrice < 0) {
						throw new InputMismatchException();
					}
					in.nextLine();
					break;
				} catch (InputMismatchException | NumberFormatException ex) {
					in.nextLine();
					System.out.println("Invalid Input Please Enter a Valid Value");
				}
			}
			int supplierID = 0;

			do {
				
				try {
					if (connect.isClosed()) {
						connect();
					}
				} catch (SQLException ex) {
					System.out.println(ex.getMessage());
				}
				
				System.out.println("Please Enter Supplier ID for the New Product");
				while (true) {
					try {
						supplierID = in.nextInt();
						in.nextLine();
						break;
					} catch (InputMismatchException | NumberFormatException ex) {
						in.nextLine();
						System.out.println("Invalid Input Please Enter a Valid Value");
					}

				}

				if (supplier.get(supplierID) == null) {

					System.out.println("This Supplier is Not Exist....");
					while (true) {
						System.out.println("""
								Press (Y) to Add A New Supplier
								Press (A) to Add Another Supplier ID
								Press (S) to Show The Supplier
								Press (E) to Back To the Main Menu
								""");
						answer = in.next().toUpperCase().charAt(0);
						if (answer != 'Y' && answer != 'E' && answer != 'S' && answer != 'A') {
							System.out.println("Invalid Answer ... ");
						}
						if (answer == 'E') {
							break Stop;
						}
						if (answer == 'S') {
							supplierService.getSuppliers();
						}
						if (answer == 'A' || answer == 'Y') {
							break;
						}
					}

					if (answer == 'Y') {
						supplierService.addSupplier();
						if (Utils.breakChar == 'E') {
							break Stop;
						}
					}
				} else {
					break;
				}
			} while (true);
			product.put(productID, new Product(productID, productName, purchasingPrice, sellingPrice, supplierID, 0));
			connect();
			String query = "insert into inventory values (?,?,?,?,?,?)";
			try {
				statement = connect.prepareStatement(query);
				statement.setInt(1, product.get(productID).getProductID());
				statement.setString(2, product.get(productID).getProduct_Name());
				statement.setFloat(3, product.get(productID).getPurchasinPrice());
				statement.setFloat(4, product.get(productID).getSellingPrice());
				statement.setInt(5, product.get(productID).getSupplierID());
				statement.setInt(6, 0);
				statement.execute();
			} catch (SQLException ex) {
				System.out.println(ex.getMessage());
			} finally {
				disconnect();
			}
			System.out.println();
			System.out.println("The Product Has Added Successfully Into the Inventory");
			System.out.println();
		}
		disconnect();

	}

	@Override
	public void updateProduct(String field) {
		connect();
		StopInventory: {
			int productID = 0;
			char answer;
			String productName = "";
			float currentPurchasingPrice = 0;
			float currentSellingPrice = 0;
			int currentSupplierID = 0;

			while (true) {
				System.out.println("Please Enter The ID of The Product");
				while (true) {

					try {
						productID = in.nextInt();
						in.nextLine();
						break;
					} catch (InputMismatchException ex) {
						in.nextLine();
						System.out.println("Invalid Input Please Enter a Valid Value");
					}
				}

				try {
					statement = connect.prepareStatement("select *from inventory where product_id = ?");
					statement.setInt(1, productID);
					resultSet = statement.executeQuery();
					if (!resultSet.next()) {
						throw new SQLException();
					}
					productName = resultSet.getString("product_name");
					currentPurchasingPrice = resultSet.getFloat("product_purchasing_price");
					currentSellingPrice = resultSet.getFloat("product_selling_price");
					currentSupplierID = resultSet.getInt("product_supplier_id");

					break;
				} catch (SQLException ex) {

					System.out.println("This Product Does Not Exist In the Inventory");
					while (true) {
						System.out.println("""
								Press (A) to Enter another Name
								Press (S) to Show Products
								Press (E) to Back to the Update Menue
								""");
						answer = in.next().toUpperCase().charAt(0);
						while (answer != 'A' && answer != 'E' && answer != 'S') {
							System.out.println("""
									Invalid Input
									Press (A) to Enter another Name
									Press (S) to Show Products
									Press (E) to Back to the Update Menue
									""");
							answer = in.next().toUpperCase().charAt(0);
						}
						if (answer == 'S') {
							getProducts();
						}
						if (answer == 'A') {
							break;
						}
						if (answer == 'E') {
							break StopInventory;
						}
					}

				}
			}

			String newName = "";
			float newPrice = 0;
			int newSupplierID = 0;
			/////////////////////////////////////////////////////////////////////////////////////////
			//////////////////////////////////////////////////////////////////////////////////////////

			if (field.toLowerCase().equals("product_name")) {
				System.out.println("Enter the New Name");
				while (true) {
					try {
						newName = in.next();

						statement = connect
								.prepareStatement("update inventory set product_name = ? where product_id = ?");
						statement.setString(1, newName);
						statement.setInt(2, productID);
						statement.execute();
						System.out.println("Product Name Has Changed From (" + productName + ") to (" + newName + ")");
						break;
					} catch (SQLException ex) {
						System.out.println("This Name is Already Used By another Product");
					}

					System.out.println("""
							Press (A) to Enter another Name
							Press (S) to Show Products
							Press (E) to Back to the Update Menue
							""");
					answer = in.next().toUpperCase().charAt(0);
					while (answer != 'A' && answer != 'E') {
						System.out.println("""
								Invalid Input
								Press (A) to Enter another Name
								Press (S) to Show Products
								Press (E) to Back to the Update Menue
								""");
						answer = in.next().toUpperCase().charAt(0);
					}
					if (answer == 'S') {
						getProducts();
					}
					if (answer == 'E') {
						break;
					}

				}

			}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

			if (field.toLowerCase().equals("product_purchasing_price")) {
				System.out.println("Enter the New Purchasing Price");
				while (true) {

					try {
						newPrice = in.nextFloat();
						if (newPrice < 0) {
							throw new InputMismatchException();
						}
						break;
					} catch (InputMismatchException ex) {
						in.nextLine();
						System.out.println("Invalid Input Please Enter a Valid Value");
					}

				}
				try {
					statement = connect
							.prepareStatement("update inventory set product_purchasing_price = ? where product_id = ?");
					statement.setFloat(1, newPrice);
					statement.setInt(2, productID);
					statement.execute();
					System.out.println("Product Purchasing Price Has Changed from (" + currentPurchasingPrice + ") to ("
							+ newPrice + ")");
				} catch (SQLException ex) {
					System.out.println(ex.getMessage());
				}

			}

			////////////////////////////////////////////////////////////////////////////////////////////////////
			/////////////////////////////////////////////////////////////////////////////////////////////////////
			if (field.toLowerCase().equals("product_selling_price")) {
				System.out.println("Enter the New Selling Price");
				while (true) {

					try {
						newPrice = in.nextFloat();
						if (newPrice < 0) {
							throw new InputMismatchException();
						}
						break;
					} catch (InputMismatchException ex) {
						in.nextLine();
						System.out.println("Invalid Input Please Enter a Valid Value");
					}

				}
				try {
					statement = connect
							.prepareStatement("update inventory set product_selling_price = ? where product_id = ?");
					statement.setFloat(1, newPrice);
					statement.setInt(2, productID);
					statement.execute();
					System.out.println("Product Selling Price Has Changed from (" + currentSellingPrice + ") to ("
							+ newPrice + ")");
				} catch (SQLException ex) {
					System.out.println(ex.getMessage());
				}
			}
			////////////////////////////////////////////////////////////////////////////////
			//////////////////////////////////////////////////////////////////////////////

			if (field.toLowerCase().equals("product_supplier_id")) {

				while (true) {
					System.out.println("Enter the New Supplier ID");

					while (true) {

						try {
							newSupplierID = in.nextInt();
							if (newSupplierID < 0) {
								throw new InputMismatchException();
							}
							in.nextLine();
							break;
						} catch (InputMismatchException ex) {
							in.nextLine();
							System.out.println("Invalid Input Please Enter a Valid Value");
						}

					}

					try {
						statement = connect.prepareStatement("select *from suppliers where id = ?");
						statement.setInt(1, newSupplierID);
						resultSet = statement.executeQuery();

						if (!resultSet.next()) {
							throw new SQLException();
						}
						break;

					} catch (SQLException ex) {
						System.out.println("This Supplier is Not Exist....");
						while (true) {
							System.out.println("""
									Press (A) to Add Another Supplier ID
									Press (S) to Show The Supplier
									Press (E) to Back To the Main Menu
									""");
							answer = in.next().toUpperCase().charAt(0);
							if (answer != 'E' && answer != 'S' && answer != 'A') {
								System.out.println("Invalid Answer ... ");
							}
							if (answer == 'E') {
								break StopInventory;
							}
							if (answer == 'A') {
								break;
							}
							if (answer == 'S') {
								supplierService.getSuppliers();
							}
						}

					}

				}
				try {
					statement = connect
							.prepareStatement("update inventory set product_supplier_id = ? where product_id = ?");
					statement.setInt(1, newSupplierID);
					statement.setInt(2, productID);
					statement.execute();
					System.out.println("Product Supplier ID Has Changed from (" + currentSupplierID + ") to ("
							+ newSupplierID + ")");
				} catch (SQLException ex) {
					System.out.println(ex.getMessage());
				}

			}

		}
		disconnect();

	}

	@Override
	public void getProducts() {
		connect();

		String query;

		query = "select *from inventory";

		ArrayList<Product> product = new ArrayList<>();
		int counter = 0;

		try {
			statement = connect.prepareStatement(query);
			resultSet = statement.executeQuery();

			while (resultSet.next()) {
				product.add(new Product(resultSet.getInt("product_id"), resultSet.getString("product_name"),
						resultSet.getFloat("Product_Purchasing_Price"), resultSet.getFloat("Product_Selling_Price"),
						resultSet.getInt("Product_Supplier_ID"), resultSet.getInt("product_quantity")));
			}

			System.out.print("Product ID");
			System.out.print("    ");

			System.out.print("Product Name");
			System.out.print("       ");

			System.out.print("Purchasing Price");
			System.out.print("    ");

			System.out.print("Selling Price");
			System.out.print("       ");

			System.out.print("Supplier ID");
			System.out.print("       ");

			System.out.print("Quantity");

			System.out.println();
			System.out.println();
			while (counter < product.size()) {
				System.out.print("|");
				System.out.print(product.get(counter).getProductID());
				System.out.print("|");
				for (int r = 12 - String.valueOf(product.get(counter).getProductID()).length(); r > 0; r--) {
					System.out.print(" ");

				}
				System.out.print("| ");
				System.out.print(product.get(counter).getProduct_Name());
				for (int r = 16 - product.get(counter).getProduct_Name().length(); r >= 0; r--) {
					System.out.print(" ");
				}
				System.out.print("|");

				System.out.print(product.get(counter).getPurchasinPrice());
				System.out.print("|");
				for (int r = 18 - String.valueOf(product.get(counter).getPurchasinPrice()).length(); r > 0; r--) {
					System.out.print(" ");

				}
				System.out.print("| ");

				System.out.print(product.get(counter).getSellingPrice());
				for (int r = 17 - String.valueOf(product.get(counter).getSellingPrice()).length(); r >= 0; r--) {
					System.out.print(" ");
				}
				System.out.print("| ");

				System.out.print(product.get(counter).getSupplierID());
				for (int r = 15 - String.valueOf(product.get(counter).getSupplierID()).length(); r >= 0; r--) {
					System.out.print(" ");
				}
				System.out.print("| ");

				System.out.print(product.get(counter).getQuantity());
				for (int r = 17 - String.valueOf(product.get(counter).getQuantity()).length(); r >= 0; r--) {

				}
				counter++;
				System.out.println();
			}
		} catch (SQLException ex) {
			System.out.println(ex.getMessage());
		} finally {
			product.clear();
			disconnect();
		}

	}

	public boolean productNameIsExist() {
        boolean checker = false;
        System.out.println("This Name is Already Used by a Product in the Inventory");
        checker = productIsExist();
        return checker;
    }
	
	public boolean productIDIsExist() {
        boolean checker = false;
        System.out.println("This Product ID is Already Used by a Product in the Inventory");
        checker = productIsExist();
        return checker;
    }
	
	public boolean productIsExist() {
		char answer ;
        boolean checker = false;
        System.out.println("This Name is Already Used by a Product in the Inventory");
        Stop:
        {
            while (true) {
                System.out.println("""
                                   Press (A) to Enter Another Name
                                   Press (S) to show the Products
                                   Press (E) to Back to The Main Menu
                                   """);
                
                answer = in.next().toUpperCase().charAt(0);
                if (answer != 'A' && answer != 'S' && answer != 'E') {
                    System.out.println("""
                                       Invalid Input .... ..... .....
                              Press (A) to Add Another Product Name
                              Press (S) to show the Products in the Inventory
                              Press (E) to Back To The Main Menu
                                       """);
                }
                if (answer == 'A') {
                	
                    break;
                }
                if (answer == 'S') {
                	getProducts();
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
