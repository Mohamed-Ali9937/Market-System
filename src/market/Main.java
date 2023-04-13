package market;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Scanner;

import market.model.Customer;
import market.model.Product;
import market.model.Supplier;
import market.service.CustomerService;
import market.service.OrderService;
import market.service.ProductService;
import market.service.PurchaseService;
import market.service.SupplierService;
import market.service.impl.CustomerServiceImpl;
import market.service.impl.OrderServiceImpl;
import market.service.impl.ProductServiceImpl;
import market.service.impl.PurchaseServiceImpl;
import market.service.impl.SupplierServiceImpl;

public class Main {

	public static void main(String[] args) {

		String productQuery = "select *from inventory order by product_id";
		String customerQuery = "select *from customers order by id";
		String supplierQuery = "select *from suppliers order by id";

		PreparedStatement statement = null;
		ResultSet resultSet = null;
		Connection connect;

		HashMap<Integer, Product> product = new HashMap<>();
		HashMap<String, Product> productByName = new HashMap<>();
		HashMap<Integer, Customer> customer = new HashMap<>();
		HashMap<Integer, Supplier> supplier = new HashMap<>();
		Scanner in = new Scanner(System.in);
		MarketDatabase database = MarketDatabase.getDatabase();

		CustomerService customerService = 
				new CustomerServiceImpl(database, in, product, productByName, supplier, customer);

		SupplierService supplierService = new SupplierServiceImpl(database, in, supplier, customerService);

		ProductService productService = 
				new ProductServiceImpl(database, in, supplierService, product, productByName, supplier);

		OrderService orderService = 
				new OrderServiceImpl(database, productService, in, customerService, customer, product);

		PurchaseService purchaseService = new PurchaseServiceImpl(database, in, product, productService);

		connect = database.connect();
		
		try {

			statement = connect.prepareStatement(productQuery);
			resultSet = statement.executeQuery();
			Product dbProduct;
			while (resultSet.next()) {
				dbProduct = new Product(resultSet.getInt("product_id"), resultSet.getString("product_name"),
						resultSet.getFloat("Product_Purchasing_Price"), resultSet.getFloat("Product_Selling_Price"),
						resultSet.getInt("Product_Supplier_ID"), resultSet.getInt("product_quantity"));
				product.put(dbProduct.getProductID(), dbProduct);
			}

			statement = connect.prepareStatement(productQuery);
			resultSet = statement.executeQuery();
			Product proName;
			while (resultSet.next()) {
				proName = new Product(resultSet.getInt("product_id"), resultSet.getString("product_name"),
						resultSet.getFloat("Product_Purchasing_Price"), resultSet.getFloat("Product_Selling_Price"),
						resultSet.getInt("Product_Supplier_ID"), resultSet.getInt("product_quantity"));
				
				productByName.put(proName.getProduct_Name(), proName);
			}

			statement = connect.prepareStatement(customerQuery);
			resultSet = statement.executeQuery();
			Customer dbCustomer;
			
			while (resultSet.next()) {
				dbCustomer = 
						new Customer(resultSet.getInt("id"), resultSet.getString("name"),
								resultSet.getString("phone"), resultSet.getString("address"));
				
				customer.put(dbCustomer.getId(), dbCustomer);
			}

			statement = connect.prepareStatement(supplierQuery);
			resultSet = statement.executeQuery();
			Supplier dbSupplier;
			while (resultSet.next()) {
				dbSupplier = new Supplier(resultSet.getInt("id"), resultSet.getString("name"), resultSet.getString("phone"),
						resultSet.getString("address"));
				supplier.put(dbSupplier.getId(), dbSupplier);
			}

		} catch (SQLException ex) {
			System.out.println(ex.getMessage());
		}
		
		database.disconnect(statement, resultSet, connect);

		boolean flag = true;
		do {

			System.out.println(
					"------------------------------------------------------------- WELCOME TO OUR MARKET -------------------------------------------------------------");
			System.out.println("""


					Please choose which of the following operations you want to perform
					1.  Sell New Order
					2.  Purchase product
					3.  Add new Product into the Inventory
					4.  Add new Customer
					5.  Add new Supplier
					6.  Show Sold Orders
					7.  Show Purchases
					8.  Show Customers
					9.  Show Suppliers
					10. Show Inventory
					11. Update Data
					12. Delete Order
					13. Delete Purchase
					14. Exist from The Program
					""");

			Stop: {
				System.out.print("Please Enter Your Choice: ");
				int option = 0;
				char answer;
				while (true) {
					try {
						option = in.nextInt();
						in.nextLine();
						break;
					} catch (InputMismatchException | NumberFormatException ex) {
						in.nextLine();
						System.out.println("Invalid Input Please Enter a Valid Value");
					}

				}
				switch (option) {

				//////////////////////////////////////////////////////////////////
				////////////////////////////////////////////////////////////////
				case 1: {
					orderService.createOrder();

					break;
				}

				//////////////////////////////////////////////////////////////////
				////////////////////////////////////////////////////////////////
				case 2: {

					purchaseService.createPurchase();
					break;
				}

				//////////////////////////////////////////////////////////////////
				////////////////////////////////////////////////////////////////
				case 3: {

					productService.addProduct();
					break;
				}

				//////////////////////////////////////////////////////////////////
				////////////////////////////////////////////////////////////////
				case 4: {
					customerService.addCustomer();

					break;
				}

				//////////////////////////////////////////////////////////////////
				////////////////////////////////////////////////////////////////
				case 5: {
					supplierService.addSupplier();
					break Stop;
				}

				//////////////////////////////////////////////////////////////////
				////////////////////////////////////////////////////////////////
				case 6: {

					boolean innerFlag = true;
					do {
						System.out.println("Please Chose Which Operation You Want to Perform");
						System.out.println("""
								1. Show Total Sold Orders
								2. Show Details For a Specific Order
								3. Back to The Main Menu
								""");

						int details;
						while (true) {
							try {
								details = in.nextInt();

								break;
							} catch (InputMismatchException ex) {
								in.next();
								System.out.println("Invalid Input Please Enter a Valid Value");
							}
						}

						switch (details) {
						case 1: {
							orderService.getOrders();
							break;
						}

						case 2: {

							while (true) {

								System.out.println("Please Enter The ID For The Order You Want To Check Its Details");
								int orderID;
								while (true) {
									try {
										orderID = in.nextInt();

										break;
									} catch (InputMismatchException ex) {
										in.nextLine();
										System.out.println("Invalid Input Please Enter a Valid Value");
									}
								}

								System.out.println();
								orderService.getOrderDetails(orderID);
								System.out.println();
								System.out.println("Do you Want To Check Another Order Details ?!");
								System.out.println("Please Answer with (Y/N)");

								answer = in.next().toUpperCase().charAt(0);
								while (answer != 'Y' && answer != 'N') {
									System.out.println("Invalid Answer... Please Answer with (Y/N)");
									answer = in.next().toUpperCase().charAt(0);
								}

								if (answer == 'N') {
									break;
								}
							}

							break;

						}
						case 3: {
							innerFlag = false;
							break;
						}

						}

					} while (innerFlag);

					break;
				}
				//////////////////////////////////////////////////////////////////
				////////////////////////////////////////////////////////////////

				case 7: {
					System.out.println("The Market Purchases");
					System.out.println();
					purchaseService.getPurchases();
					System.out.println();
					break;
				}

				//////////////////////////////////////////////////////////////////
				////////////////////////////////////////////////////////////////
				//////////////////////////////////////////////////////////////
				////////////////////////////////////////////////////////////
				case 8: {
					System.out.println("Our Current Customers");
					System.out.println();
					customerService.getCustomers();
					System.out.println();
					break;
				}

				//////////////////////////////////////////////////////////////////
				////////////////////////////////////////////////////////////////
				case 9: {
					System.out.println("Our Products Suppliers");
					System.out.println();
					supplierService.getSuppliers();
					System.out.println();
					break;
				}

				//////////////////////////////////////////////////////////////////
				////////////////////////////////////////////////////////////////
				case 10: {
					System.out.println("Current Prodects In The Invenroty");
					System.out.println();
					productService.getProducts();
					System.out.println();
					break;
				}

				//////////////////////////////////////////////////////////////////
				////////////////////////////////////////////////////////////////
				case 11: {
					boolean updateFlag = true;
					do {
						System.out.println("Please Choose Which Data You Want To Update");
						System.out.println();
						System.out.println("""
								1. Update Customer
								2. Update Supplier
								3. Update Product
								4. Update Order
								5. Update Purchase
								6. Back To The Mean Menu
								""");
						int updateChoice;
						while (true) {
							try {
								updateChoice = in.nextInt();

								break;
							} catch (InputMismatchException ex) {
								in.nextLine();
								System.out.println("Invalid Input Please Enter a Valid Value");
							}
						}

						switch (updateChoice) {

						case 1: {
							boolean customerFlag = true;
							do {
								System.out.println("""
										Chose Which Data You Want To Update To a Customer
										1. Name
										2. Phone
										3. Address
										4. To Back To Update Menu
										""");
								int customerChoice;
								while (true) {
									try {
										customerChoice = in.nextInt();

										break;
									} catch (InputMismatchException ex) {
										in.nextLine();
										System.out.println("Invalid Input Please Enter a Valid Value");
									}
								}

								switch (customerChoice) {

								case 1: {
									customerService.updateCustomer("name");
									break;
								}

								case 2: {
									customerService.updateCustomer("phone");
									break;
								}

								case 3: {

									customerService.updateCustomer("address");
									break;
								}

								case 4: {
									customerFlag = false;
									break;
								}
								}

							} while (customerFlag);

							break;
						}

						case 2: {
							boolean supplierFlag = true;
							do {
								System.out.println("""
										Chose Which Data You Want To Update To a Customer
										1. Name
										2. Phone
										3. Address
										4. To Back To Update Menu
										""");
								int supplierChoice;
								while (true) {
									try {
										supplierChoice = in.nextInt();

										break;
									} catch (InputMismatchException ex) {
										in.nextLine();
										System.out.println("Invalid Input Please Enter a Valid Value");
									}
								}

								switch (supplierChoice) {

								case 1: {
									supplierService.updateSupplier("name");
									break;
								}

								case 2: {
									supplierService.updateSupplier("phone");
									break;
								}

								case 3: {

									supplierService.updateSupplier("address");
									break;
								}

								case 4: {
									supplierFlag = false;
									break;
								}
								}
							} while (supplierFlag);

							break;
						}
						case 3: {
							boolean productFlag = true;
							do {
								System.out.println("""
										Chose Which Data You Want To Update To a Product
										1. Product Name
										2. Purchasing Price
										3. Selling Price
										4. Product Supplier
										5. To Back To Update Menu
										""");
								int productChoice;
								while (true) {
									try {
										productChoice = in.nextInt();

										break;
									} catch (InputMismatchException ex) {
										in.nextLine();
										System.out.println("Invalid Input Please Enter a Valid Value");
									}
								}

								switch (productChoice) {

								case 1: {
									productService.updateProduct("Product_Name");
									break;
								}

								case 2: {
									productService.updateProduct("Product_Purchasing_Price");
									break;
								}

								case 3: {

									productService.updateProduct("Product_Selling_Price");
									break;
								}

								case 4: {
									productService.updateProduct("Product_Supplier_ID");
									break;
								}

								case 5: {
									productFlag = false;
									break;
								}
								}

							} while (productFlag);
							break;
						}

						case 4: {
							boolean orderFlag = true;
							do {
								System.out.println("""
										Chose Which Data You Want To Update To an Order
										1. Date
										2. Customer ID
										3. Quantity
										4. Price
										5. To Back To Update Menu
										""");
								int orderChoice;
								while (true) {
									try {
										orderChoice = in.nextInt();

										break;
									} catch (InputMismatchException ex) {
										in.nextLine();
										System.out.println("Invalid Input Please Enter a Valid Value");
									}
								}

								switch (orderChoice) {

								case 1: {
									orderService.updateOrder("orders", "date");
									break;
								}

								case 2: {
									orderService.updateOrder("orders", "customer_id");
									break;
								}

								case 3: {

									orderService.updateOrder("order_details", "quantity");
									break;
								}

								case 4: {
									orderService.updateOrder("order_details", "price");
									break;
								}

								case 5: {
									orderFlag = false;
									break;
								}
								}

							} while (orderFlag);
							break;
						}

						case 5: {

							boolean purchaseFlag = true;
							do {
								System.out.println("""
										Chose Which Data You Want To Update To a Purchase
										1. Date
										2. Product ID
										3. Price
										4. Quantity
										5. To Back To Update Menu
										""");
								int purchaseChoice;
								while (true) {
									try {
										purchaseChoice = in.nextInt();

										break;
									} catch (InputMismatchException ex) {
										in.nextLine();
										System.out.println("Invalid Input Please Enter a Valid Value");
									}
								}

								switch (purchaseChoice) {

								case 1: {
									purchaseService.updatePurchase("date");
									break;
								}

								case 2: {
									purchaseService.updatePurchase("product_id");
									break;
								}

								case 3: {
									purchaseService.updatePurchase("price");
									break;
								}

								case 4: {
									purchaseService.updatePurchase("quantity");
									break;
								}

								case 5: {
									purchaseFlag = false;
									break;
								}
								}

							} while (purchaseFlag);

							break;
						}
						case 6: {
							updateFlag = false;
							break;
						}
						}
					} while (updateFlag);

					break;
				}

				//////////////////////////////////////////////////////////////////
				////////////////////////////////////////////////////////////////
				case 12: {

					orderService.deleteOrder();
					break;
				}

				case 13: {
					purchaseService.deletePurchase();
					break;
				}

				case 14: {
					flag = false;
					break;
				}
				}
			}

		} while (flag);

	}
}
