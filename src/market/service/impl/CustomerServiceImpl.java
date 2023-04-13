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
import market.model.Customer;
import market.model.Product;
import market.model.Supplier;
import market.service.CustomerService;

public class CustomerServiceImpl implements CustomerService {

	private MarketDatabase database;
	private Connection connect;
	private PreparedStatement statement;
	private Scanner in;
	private ResultSet resultSet;
	HashMap<Integer, Product> product;
	HashMap<String, Product> productByName;
	HashMap<Integer, Supplier> supplier;
	HashMap<Integer, Customer> customer;

	
	
	public CustomerServiceImpl(MarketDatabase database, Scanner in, 
			HashMap<Integer, Product> product, HashMap<String, Product> productByName,
			HashMap<Integer, Supplier> supplier, HashMap<Integer, Customer> customer) {
		
		this.database = database;
		this.in = in;
		this.product = product;
		this.productByName = productByName;
		this.supplier = supplier;
		this.customer = customer;
	}

	private void connect() {
		this.connect = database.connect();
	}

	@Override
	public void addCustomer() {
		Stop: {
			
			Utils.breakChar = ' ';
			
			connect();
			int newCusID = 0;
			do {
				
				try {
					if (connect.isClosed()) {
						connect();
					}
				} catch (SQLException ex) {
					System.out.println(ex.getMessage());
				}
				
				System.out.print("Please Enter the New Customer ID: ");

				while (true) {
					try {
						newCusID = in.nextInt();
						if (newCusID < 0) {
							throw new InputMismatchException();
						}
						break;
					} catch (InputMismatchException | NumberFormatException ex) {
						in.nextLine();
						System.out.println("Invalid Input Please Enter a Valid Value");
					}

				}

				if (customer.get(newCusID) != null) {
					if (customerIsExist()) {
						Utils.breakChar = 'E';
						break Stop;
					}
				} else {
					break;
				}
			} while (true);

			in.nextLine();
			System.out.print("Please Enter the New Customer Name: ");
			String newCusName = in.nextLine();

			System.out.print("Please Enter the New Customer Phone: ");
			String newCusPhone = in.nextLine();

			System.out.print("Please Enter the New Customer Address: ");
			String newCusAddress = in.nextLine();

			customer.put(newCusID, new Customer(newCusID, newCusName, newCusPhone, newCusAddress));
			String query = "insert into customers values(?,?,?,?)";
			try {
				statement = connect.prepareStatement(query);
				statement.setInt(1, customer.get(newCusID).getId());
				statement.setString(2, customer.get(newCusID).getName());
				statement.setString(3, customer.get(newCusID).getPhone());
				statement.setString(4, customer.get(newCusID).getAddress());
				statement.execute();
			} catch (SQLException ex) {
				System.out.println(ex.getMessage());
			} finally {
				disconnect();
			}
		}

	}

	@Override
	public void getCustomers() {
		connect();
		ArrayList<Customer> customerList = new ArrayList<>();
		ArrayList<Float> totalOrdersPrice = new ArrayList<>();
		String query;

		query = """
				select customers.id , customers.name, customers.phone, customers.address, sum(orders.order_total_price) as total from
				customers left join orders on customers.id = orders.customer_id group by customers.id
				""";

		try {
			statement = connect.prepareStatement(query);
			resultSet = statement.executeQuery();

			while (resultSet.next()) {
				customerList.add(new Customer(resultSet.getInt("id"), resultSet.getString("name"),
						resultSet.getString("phone"), resultSet.getString("address")));
				totalOrdersPrice.add(resultSet.getFloat("total"));

			}
			System.out.print("Customer ID");
			System.out.print("    ");

			System.out.print("Customer Name");
			System.out.print("          ");

			System.out.print("Customer Phone");

			System.out.print("         ");
			System.out.print("Customer Address");
			System.out.print("       ");
			System.out.print("Customer Orders Total Amount");

			System.out.println();
			System.out.println();

			customerList.forEach(customer -> {
				System.out.print("|");
				System.out.print(customer.getId());
				System.out.print("|");
				for (int r = 13 - String.valueOf(customer.getId()).length(); r > 0; r--) {
					System.out.print(" ");
				}
				System.out.print("|");
				System.out.print(customer.getName());
				for (int r = 20 - customer.getName().length(); r >= 0; r--) {
					System.out.print(" ");
				}
				System.out.print("| ");
				System.out.print(customer.getPhone());
				for (int r = 20 - customer.getPhone().length(); r >= 0; r--) {
					System.out.print(" ");
				}
				System.out.print("| ");

				System.out.print(customer.getAddress());
				for (int r = 20 - customer.getAddress().length(); r >= 0; r--) {
					System.out.print(" ");
				}
				System.out.print("| ");

				System.out.print(totalOrdersPrice.get(0));
				for (int r = 7 - String.valueOf(totalOrdersPrice.get(0)).length(); r >= 0; r--) {
					System.out.print(" ");
				}
				totalOrdersPrice.remove(0);
				System.out.println();
			});

//            while (counter < customerList.size()) {
//                System.out.print("|");
//
//                System.out.print(customerList.get(counter).getId());
//                System.out.print("|");
//                for (int r = 13 - String.valueOf(customerList.get(counter).getId()).length(); r > 0; r--) {
//                    System.out.print(" ");
//
//                }
//                System.out.print("| ");
//
//                System.out.print(customerList.get(counter).getName());
//                for (int r = 20 - customerList.get(counter).getName().length(); r >= 0; r--) {
//                    System.out.print(" ");
//                }
//                System.out.print("| ");
//                System.out.print(customerList.get(counter).getPhone());
//                for (int r = 20 - customerList.get(counter).getPhone().length(); r >= 0; r--) {
//                    System.out.print(" ");
//                }
//                System.out.print("| ");
//                System.out.print(customerList.get(counter).getAddress());
//                for (int r = 20 - customerList.get(counter).getAddress().length(); r >= 0; r--) {
//                    System.out.print(" ");
//                }
//                System.out.print("| ");
//
//                System.out.print(totalOrdersPrice.get(counter));
//                for (int r = 7 - String.valueOf(totalOrdersPrice.get(counter)).length(); r >= 0; r--) {
//                    System.out.print(" ");
//                }
//
//                System.out.println();
//                counter++;
//
//            }
		} catch (SQLException ex) {
			System.out.println(ex.getMessage());
		} finally {
			customerList.clear();
			totalOrdersPrice.clear();
			disconnect();
		}
	}

	@Override
	public void updateCustomer(String field) {
		connect();
		StopCustomer: {
			char answer;
			int customerID = 0;
			String customerCurrentName = "";
			String customerCurrentPhone = "";
			String customerCurrentAddress = "";

			while (true) {
				System.out.println("Please Enter The ID of The Customer");
				while (true) {

					try {
						customerID = in.nextInt();
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
					
					statement = connect.prepareStatement("select *from customers where id = ?");
					statement.setInt(1, customerID);
					resultSet = statement.executeQuery();
					if (!resultSet.next()) {
						throw new SQLException();
					}
					customerCurrentName = resultSet.getString("name");
					customerCurrentPhone = resultSet.getString("phone");
					customerCurrentAddress = resultSet.getString("address");

					break;
				} catch (SQLException ex) {

					System.out.println("This Customer Does Not Exist");
					while (true) {
						System.out.println("""
								Press (A) to Enter another ID
								Press (S) to Show Customers
								Press (E) to Back to the Update Menu
								""");
						answer = in.next().toUpperCase().charAt(0);
						while (answer != 'A' && answer != 'E' && answer != 'S') {
							System.out.println("""
									Invalid Input
									Press (A) to Enter another ID
									Press (S) to Show Customers
									Press (E) to Back to the Update Menu
									""");
							answer = in.next().toUpperCase().charAt(0);
						}
						if (answer == 'S') {
							getCustomers();
						}
						if (answer == 'A') {
							break;
						}
						if (answer == 'E') {
							break StopCustomer;
						}
					}

				}
			}

			if (field.toLowerCase().equals("name")) {
				System.out.println("The current name of the customer is: " + customerCurrentName);
				System.out.println("Enter the New Customer Name");
				String newName = in.nextLine();
				try {
					statement = connect.prepareStatement("update customers set name = ? where id = ?");
					statement.setString(1, newName);
					statement.setInt(2, customerID);
					statement.execute();
					System.out.println("The Name of the Customer Has Changed from "
							+ "(" + customerCurrentName + ") to (" + newName + ") ");
				} catch (SQLException ex) {
					System.out.println(ex.getMessage());
				}
			}

			if (field.toLowerCase().equals("phone")) {
				System.out.println("Enter the New Customer phone");
				String newPhone = in.nextLine();
				try {
					statement = connect.prepareStatement("update customers set phone = ? where id = ?");
					statement.setString(1, newPhone);
					statement.setInt(2, customerID);
					statement.execute();
					System.out.println("The Phone of the Customer Has Changed from(" + customerCurrentPhone + ") to ("
							+ newPhone + ") ");
				} catch (SQLException ex) {
					System.out.println(ex.getMessage());
				}
			}

			if (field.toLowerCase().equals("address")) {
				System.out.println("Enter the New Customer Address");
				String newAddress = in.nextLine();
				try {
					statement = connect.prepareStatement("update customers set address = ? where id = ?");
					statement.setString(1, newAddress);
					statement.setInt(2, customerID);
					statement.execute();

					System.out.println("The Address of the Customer Has Changed from(" + customerCurrentAddress
							+ ") to (" + newAddress + ") ");
				} catch (SQLException ex) {
					System.out.println(ex.getMessage());
				}
			}

		}
		disconnect();

	}

	private void disconnect() {
		database.disconnect(statement, resultSet, connect);
	}

	public boolean customerIsExist() {
        boolean checker = false;
        char answer;
        Stop:
        {

            System.out.println("This ID is Already Used by another Customer");
            while (true) {
                System.out.println("""
                                   Press (A) to Enter Another ID
                                   Press (S) to show the Customers
                                   Press (E) to Back to The Main Menu """);
                answer = in.next().toUpperCase().charAt(0);
                if (answer != 'A' && answer != 'S' && answer != 'E') {
                    System.out.println("Invalid Input ....");
                }
                if (answer == 'A') {
                    break;
                }
                if (answer == 'S') {
                    getCustomers();
                }
                if (answer == 'E') {
                    checker = true;
                    break Stop;
                }
            }
        }
        return checker;
    }

}
