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
import market.model.Supplier;
import market.service.CustomerService;
import market.service.SupplierService;

public class SupplierServiceImpl implements SupplierService {

	private char breakChar;
	private MarketDatabase database;
	private Connection connect;
	private PreparedStatement statement;
	private Scanner in;
	private ResultSet resultSet;
	private HashMap<Integer, Supplier> supplier;
	private CustomerService customerService;

	public SupplierServiceImpl(MarketDatabase database, Scanner in,
			HashMap<Integer, Supplier> supplier, CustomerService customerService) {

		this.database = database;
		this.in = in;
		this.supplier = supplier;
		this.customerService = customerService;
	}

	private void connect() {
		this.connect = database.connect();
	}

	@Override
	public void addSupplier() {

		Stop: {
			connect();
			int newSupplierID = 0;
			Utils.breakChar = ' ';
			boolean check;

			do {
				System.out.print("Please Enter the New Supplier ID: ");
				
				try {
					if (connect.isClosed()) {
						connect();
					}
				} catch (SQLException ex) {
					System.out.println(ex.getMessage());
				}
				
				check = false;

				while (!check) {
					try {
						newSupplierID = in.nextInt();

						check = true;
					} catch (InputMismatchException | NumberFormatException ex) {
						in.nextLine();
						System.out.println("Invalid Input Please Enter a Valid Value");
					}

				}

				if (supplier.get(newSupplierID) != null) {
					if (supplierIsExist()) {
						Utils.breakChar = 'E';
						break Stop;
					}
				} else {
					break;
				}
			} while (true);

			in.nextLine();
			System.out.print("Please Enter the New Supplier Name: ");
			String newSupplierName = in.nextLine();

			System.out.print("Please Enter the New Supplier Phone: ");
			String newSupplierPhone = in.nextLine();

			System.out.print("Please Enter the New Supplier Address: ");
			String newSupplierAddress = in.nextLine();

			supplier.put(newSupplierID,
					new Supplier(newSupplierID, newSupplierName, newSupplierPhone, newSupplierAddress));

			String query = "insert into suppliers values(?,?,?,?)";
			try {
				statement = connect.prepareStatement(query);
				statement.setInt(1, supplier.get(newSupplierID).getId());
				statement.setString(2, supplier.get(newSupplierID).getName());
				statement.setString(3, supplier.get(newSupplierID).getPhone());
				statement.setString(4, supplier.get(newSupplierID).getAddress());
				statement.execute();
			} catch (SQLException ex) {
				System.out.println(ex.getMessage());
			} finally {
				disconnect();
			}
		}

	}

	@Override
	public char getBreakChar() {
		return breakChar;
	}

	@Override
	public void getSuppliers() {
		connect();
		ArrayList<Supplier> supplierList = new ArrayList<>();
		ArrayList<Float> totalOrdersPrice = new ArrayList<>();
		String query;

		query = """
				select suppliers.id, suppliers.name , suppliers.phone, suppliers.address, sum(purchases.total_price) as
				Total_Purchases_Amount from
				suppliers left join purchases on purchases.supplier_id = suppliers.id group by suppliers.id;
				""";

		try {
			statement = connect.prepareStatement(query);
			resultSet = statement.executeQuery();
			int counter = 0;
			while (resultSet.next()) {
				supplierList.add(new Supplier(resultSet.getInt("id"), resultSet.getString("name"),
						resultSet.getString("phone"), resultSet.getString("address")));
				totalOrdersPrice.add(resultSet.getFloat("Total_Purchases_Amount"));

			}
			System.out.print("Supplier ID");
			System.out.print("    ");

			System.out.print("Supplier Name");
			System.out.print("          ");

			System.out.print("Supplier Phone");

			System.out.print("         ");
			System.out.print("Supplier Address");
			System.out.print("       ");
			System.out.print("Purchases Total Amount");

			System.out.println();
			System.out.println();

			while (counter < supplierList.size()) {
				System.out.print("|");

				System.out.print(supplierList.get(counter).getId());
				System.out.print("|");
				for (int r = 13 - String.valueOf(supplierList.get(counter).getId()).length(); r > 0; r--) {
					System.out.print(" ");

				}
				System.out.print("| ");

				System.out.print(supplierList.get(counter).getName());
				for (int r = 20 - supplierList.get(counter).getName().length(); r >= 0; r--) {
					System.out.print(" ");
				}
				System.out.print("| ");
				System.out.print(supplierList.get(counter).getPhone());
				for (int r = 20 - supplierList.get(counter).getPhone().length(); r >= 0; r--) {
					System.out.print(" ");
				}
				System.out.print("| ");
				System.out.print(supplierList.get(counter).getAddress());
				for (int r = 20 - supplierList.get(counter).getAddress().length(); r >= 0; r--) {
					System.out.print(" ");
				}
				System.out.print("| ");

				System.out.print(totalOrdersPrice.get(counter));
				for (int r = 7 - String.valueOf(totalOrdersPrice.get(counter)).length(); r >= 0; r--) {
					System.out.print(" ");
				}

				System.out.println();
				counter++;

			}
		} catch (SQLException ex) {
			System.out.println(ex.getMessage());
		} finally {
			totalOrdersPrice.clear();
			supplierList.clear();
			disconnect();
		}

	}

	@Override
	public void updateSupplier(String field) {
		connect();
		StopSupplier: {
			char answer;
			int supplierID = 0;
			String supplierCurrentName = "";
			String supplierCurrentPhone = "";
			String supplierCurrentAddress = "";

			while (true) {
				System.out.println("Please Enter The ID of The Supplier");
				while (true) {

					try {
						supplierID = in.nextInt();
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
					
					statement = connect.prepareStatement("select *from suppliers where id = ?");
					statement.setInt(1, supplierID);
					resultSet = statement.executeQuery();
					if (!resultSet.next()) {
						throw new SQLException();
					}
					supplierCurrentName = resultSet.getString("name");
					supplierCurrentPhone = resultSet.getString("phone");
					supplierCurrentAddress = resultSet.getString("address");

					break;
				} catch (SQLException ex) {

					System.out.println("This Supplier Does Not Exist");
					while (true) {
						System.out.println("""
								Press (A) to Enter another ID
								Press (S) to Show Suppliers
								Press (E) to Back to the Update Menue
								""");
						answer = in.next().toUpperCase().charAt(0);
						while (answer != 'A' && answer != 'E' && answer != 'S') {
							System.out.println("""
									Invalid Input
									Press (A) to Enter another ID
									Press (S) to Show suppliers
									Press (E) to Back to the Update Menue
									""");
							answer = in.next().toUpperCase().charAt(0);
						}
						if (answer == 'S') {
							customerService.getCustomers();
						}
						if (answer == 'A') {
							break;
						}
						if (answer == 'E') {
							break StopSupplier;
						}
					}

				}
			}

			if (field.toLowerCase().equals("name")) {
				
				System.out.println("The current name of this supplier is: " + supplierCurrentName);
				
				System.out.println("Enter the New Supplier Name");
				String newName = in.nextLine();
				try {
					statement = connect.prepareStatement("update suppliers set name = ? where id = ?");
					statement.setString(1, newName);
					statement.setInt(2, supplierID);
					statement.execute();
					System.out.println("The Name of the Supplier Has Changed from(" + supplierCurrentName + ") to ("
							+ newName + ") ");
				} catch (SQLException ex) {
					System.out.println(ex.getMessage());
				}
			}

			if (field.toLowerCase().equals("phone")) {
				System.out.println("Enter the New Supplier phone");
				String newPhone = in.nextLine();
				try {
					statement = connect.prepareStatement("update suppliers set phone = ? where id = ?");
					statement.setString(1, newPhone);
					statement.setInt(2, supplierID);
					statement.execute();
					System.out.println("The Phone of the Supplier Has Changed from(" + supplierCurrentPhone + ") to ("
							+ newPhone + ") ");
				} catch (SQLException ex) {
					System.out.println(ex.getMessage());
				}
			}

			if (field.toLowerCase().equals("address")) {
				System.out.println("Enter the New Supplier Address");
				String newAddress = in.nextLine();
				try {
					statement = connect.prepareStatement("update suppliers set address = ? where id = ?");
					statement.setString(1, newAddress);
					statement.setInt(2, supplierID);
					statement.execute();

					System.out.println("The Address of the Supplier Has Changed from(" + supplierCurrentAddress
							+ ") to (" + newAddress + ") ");
				} catch (SQLException ex) {
					System.out.println(ex.getMessage());
				}
			}

		}
		disconnect();

	}
	
	public boolean supplierIsExist() {
        boolean checker = false;
        char answer;
        Stop:
        {
            System.out.println("This ID is Already Used by another Supplier");
            while (true) {
                System.out.println("""
                                   Press (A) to Enter Another ID
                                   Press (S) to show the Suppliers
                                   Press (E) to Back to The Main Menu
                                   """);
                answer = in.next().toUpperCase().charAt(0);
                if (answer != 'A' && answer != 'S' && answer != 'E') {
                    System.out.println("Invalid Input ....");
                }
                if (answer == 'A') {
                    break;
                }
                if (answer == 'S') {
                    getSuppliers();
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
