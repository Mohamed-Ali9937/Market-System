package market.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Scanner;

import market.Utils;
import market.MarketDatabase;
import market.model.Customer;
import market.model.Order;
import market.model.Product;
import market.service.CustomerService;
import market.service.OrderService;
import market.service.ProductService;

public class OrderServiceImpl implements OrderService {

	private MarketDatabase database;
	private Connection connect;
	private PreparedStatement statement;
	private Scanner in;
	private ResultSet resultSet;
	private CustomerService customerService;
	private LocalDate date = LocalDate.now();
	private ProductService productService;
	private HashMap <Integer, Customer> customer;
	private HashMap<Integer, Product> product;
	
	
	public OrderServiceImpl(MarketDatabase database, ProductService productService, Scanner in,
			CustomerService customerService,
			HashMap<Integer, Customer> customer, HashMap<Integer, Product> product) {
		this.database = database;
		this.productService = productService;
		this.in = in;
		this.customerService = customerService;
		this.customer = customer;
		this.product = product;
	}

	private void connect() {
		this.connect = database.connect();
	}
	
	@Override
	public void createOrder() {
		Stop:
        {
            char answer;

            connect();

            Utils.breakChar = ' ';
            
            int orderID;

            try {
                do {
                	
                	if (connect.isClosed()) {
                    	connect();
                    }
                	
                    System.out.println("Please Enter the Order ID");

                    while (true) {
                        try {
                            orderID = in.nextInt();
                            in.nextLine();
                            if (orderID < 0) {
                                throw new InputMismatchException();
                            }
                            break;
                        } catch (InputMismatchException | NumberFormatException ex) {
                            in.nextLine();
                            System.out.println("Invalid Input Please Enter a Valid Value");
                        }

                    }
                    statement = this.connect.prepareStatement("select *from orders where id = ?");
                    statement.setInt(1, orderID);
                    resultSet = statement.executeQuery();
                    if (resultSet.next()) {
                        System.out.println("This Order ID is Already Exists...");
                        if (orderIsExist()) {
                            break Stop;
                        }
                    } else {
                        break;
                    }

                } while (true);

                
                int customerID = 0;

                do {
                	
                	if (connect.isClosed()) {
                    	connect();
                    }
                	
                    System.out.println("Please Enter the Customer ID ");

                    while (true) {
                        try {
                            customerID = in.nextInt();
                            in.nextLine();
                            break;
                        } catch (InputMismatchException | NumberFormatException ex) {
                            in.nextLine();
                            System.out.println("Invalid Input Please Enter a Valid Value");
                        }

                    }

                    if (customer.get(customerID) == null) {

                        System.out.println("This Customer is Not Exist....");
                        while (true) {
                        	
                            System.out.println("""
                                           Press (Y) to Add A New Customer
                                           Press (A) to Add Another Customer ID
                                           Press (S) to Show The Customers
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
                                customerService.getCustomers();;
                            }
                            if (answer == 'A' || answer == 'Y') {
                                break;
                            }
                        }
                        if (answer == 'Y') {
                            customerService.addCustomer();
                            if (Utils.breakChar  == 'E') {
                                break Stop;
                            }
                        }
                    } else {
                        break;
                    }
                } while (true);
                Order o = new Order(orderID, date, customer.get(customerID));
                do {
                    Outter:
                    {
                        do {
                        	
                        	if (connect.isClosed()) {
                            	connect();
                            }

                            int proID;
                            do {
                                System.out.println("Please Enter The Prodect ID");
                                proID = 0;

                                while (true) {
                                    try {
                                        proID = in.nextInt();
                                        in.nextLine();
                                        break;
                                    } catch (InputMismatchException | NumberFormatException ex) {
                                        in.nextLine();
                                        System.out.println("Invalid Input Please Enter a Valid Value");
                                    }

                                }

                                if (product.get(proID) == null) {
                                    if (productIsNotExist()) {
                                        break Stop;
                                    }
                                } else {
                                    break;
                                }
                            } while (true);
                            System.out.println(product.get(proID).getProduct_Name());
                            Product n = new Product(product.get(proID).getProductID(), product.get(proID).getProduct_Name(),
                                    product.get(proID).getPurchasinPrice(), product.get(proID).getSellingPrice(),
                                    product.get(proID).getSupplierID(), product.get(proID).getQuantity());

                            int quantity;
                            do {
                                System.out.println("Please Enter Quantity");
                                quantity = 0;

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
                                if (n.getQuantity() < quantity) {
                                    System.out.println("This Quantity is not avilable in the store... "
                                            + "Only (" + n.getQuantity() + ") are Avilable at the Moment");

                                    while (true) {
                                        System.out.println("""
                                                           press (A) to Change The Quantity
                                                           Press (P) to Change The Product
                                                           Press (E) to Back to the Mean Menu                                                        
                                                           """);
                                        answer = in.next().toUpperCase().charAt(0);
                                        if (answer != 'E' && answer != 'A' && answer != 'P') {
                                            System.out.println("Invalid Input ...");
                                        }

                                        if (answer == 'E') {
                                            break Stop;
                                        }
                                        if (answer == 'A') {
                                            break;
                                        }
                                        if (answer == 'P') {
                                            break Outter;
                                        }
                                    }
                                } else {
                                    break;
                                }
                            } while (true);

                            n.setQuantity(quantity);
                            o.setProducts(n);
                            System.out.println("Do You Want To Insert Another Product .. ? Please Answer With (Y/N)");
                            answer = in.next().toUpperCase().charAt(0);

                        } while (answer != 'N');
                        break;
                    }

                } while (true);
                String orderQuery = "insert into orders values(?,?,?,?)";

                statement = connect.prepareStatement(orderQuery);
                statement.setInt(1, o.getId());
                statement.setString(2, o.getDate().toString());
                statement.setInt(3, o.getCustomer().getId());
                statement.setFloat(4, o.getOrderPrice());
                statement.execute();

                for (int i = 0; i < o.getProducts().size(); i++) {
                    String orderDetailsQuery = "insert into order_details values(?,?,?,?)";
                    statement = connect.prepareStatement(orderDetailsQuery);
                    statement.setInt(1, o.getId());
                    statement.setInt(2, o.getProducts().get(i).getProductID());
                    statement.setFloat(3, o.getProducts().get(i).getSellingPrice());
                    statement.setInt(4, o.getProducts().get(i).getQuantity());
                    statement.execute();
                    String updateInventoryQuery = "update inventory set product_quantity = product_quantity - ? where product_id = ?";
                    statement = connect.prepareStatement(updateInventoryQuery);
                    statement.setInt(1, o.getProducts().get(i).getQuantity());
                    statement.setInt(2, o.getProducts().get(i).getProductID());
                    statement.execute();
                }

            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            } finally {
                disconnect();
            }
        }
		
	}

	@Override
	public void getOrders() {
		connect();
        float totalSales = 0;
        String query;
        
        ArrayList<Customer> customerList = new ArrayList<>();
        ArrayList<Order> orderList = new ArrayList<>();
        query = "select  orders.id, orders.date,customers.id,customers.name, orders.order_total_price"
                + " from orders join customers on orders.Customer_ID = customers.id order by orders.ID";
        try {
            statement = connect.prepareStatement(query);
            resultSet = statement.executeQuery();
            int counter = 0;
            while (resultSet.next()) {
                customerList.add(new Customer(resultSet.getInt("customers.id"), 
                		resultSet.getString("customers.name")));
                
                orderList.add(new Order(resultSet.getInt("orders.id"), resultSet.getDate("orders.date").toLocalDate(),
                		resultSet.getFloat("orders.order_total_price")));

            }

            System.out.print("Order ID");
            System.out.print("    ");

            System.out.print("Order Date");
            System.out.print("         ");
            System.out.print("Customer ID");
            System.out.print("       ");

            System.out.print("Customer Name");
            System.out.print("          ");

            System.out.print("Order Total Price");
            System.out.println();
            System.out.println();
            while (counter < customerList.size()) {
                System.out.print("|");
                System.out.print(orderList.get(counter).getId());
                System.out.print("|");
                for (int r = 10 - String.valueOf(orderList.get(counter).getId()).length(); r > 0; r--) {
                    System.out.print(" ");
                }
                System.out.print("| ");
                System.out.print(orderList.get(counter).getDate());
                for (int r = 16 - orderList.get(counter).getDate().toString().length(); r >= 0; r--) {
                    System.out.print(" ");
                }
                System.out.print("|");

                System.out.print(customerList.get(counter).getId());
                System.out.print("|");
                for (int r = 16 - String.valueOf(customerList.get(counter).getId()).length(); r > 0; r--) {
                    System.out.print(" ");

                }
                System.out.print("| ");

                System.out.print(customerList.get(counter).getName());
                for (int r = 20 - customerList.get(counter).getName().length(); r >= 0; r--) {
                    System.out.print(" ");
                }
                System.out.print("| ");

                System.out.print(orderList.get(counter).getOrderPrice());
                for (int r = 7 - String.valueOf(orderList.get(counter).getOrderPrice()).length(); r >= 0; r--) {
                    System.out.print(" ");
                }

                System.out.println();
                totalSales += orderList.get(counter).getOrderPrice();
                counter++;
            }

            System.out.println("----------------------------------------------------------------------------------------");
            System.out.println("TOTAl SALES                                                             | " + totalSales);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());

        } finally {
            orderList.clear();
            customerList.clear();
            disconnect();
        }
		
	}

	@Override
	public void getOrderDetails(int orderId) {
		connect();
        HashMap<Integer, ResultSet> order = new HashMap<>();
        String query;
        String detailedQuery = "";
        String printer = "";
        PreparedStatement detailsStatement = null;
        ResultSet detailsResult = null;
        ArrayList<Customer> customerList = new ArrayList<>();
        ArrayList<Order> orderList = new ArrayList<>();
        query = "select  orders.id, orders.date,customers.id,customers.name, orders.order_total_price"
                + " from orders join customers on orders.id = ? and orders.Customer_ID = customers.id";
        try {
            statement = connect.prepareStatement(query);
            statement.setInt(1, orderId);
            resultSet = statement.executeQuery();
            int counter = 0;
            while (resultSet.next()) {
                customerList.add(new Customer(resultSet.getInt("customers.id"), 
                		resultSet.getString("customers.name")));
                
                orderList.add(new Order(resultSet.getInt("orders.id"), 
                		resultSet.getDate("orders.date").toLocalDate(),
                		resultSet.getFloat("orders.order_total_price")));

            }
            if(customerList.isEmpty() && orderList.isEmpty()){
                throw new SQLDataException();
            }
            
            int innerCounter = 0;
            int[] rowsNumber = new int[customerList.size()];

            while (innerCounter < customerList.size()) {
                detailedQuery = "select inventory.product_name, order_details.quantity, order_details.price "
                        + "from order_details join orders join inventory join customers"
                        + " on order_details.order_id = ? and order_details.order_id = orders.id"
                        + " and orders.customer_id = customers.id and inventory.product_id = order_details.product_id";
                detailsStatement = connect.prepareStatement(detailedQuery);
                detailsStatement.setInt(1, orderId);
                detailsResult = detailsStatement.executeQuery();
                order.put(innerCounter + 1, detailsResult);

                innerCounter++;
            }
            int headsCounter = 0;
            while (headsCounter < customerList.size()) {
                detailedQuery = "select count(*) as count from order_details where order_id = ?";
                detailsStatement = connect.prepareStatement(detailedQuery);
                detailsStatement.setInt(1, orderId);
                detailsResult = detailsStatement.executeQuery();
                while (detailsResult.next()) {
                    rowsNumber[headsCounter] = detailsResult.getInt("count");
                }
                headsCounter++;
            }
            Arrays.sort(rowsNumber);
            System.out.print("Order ID");
            System.out.print("    ");

            System.out.print("Order Date");
            System.out.print("         ");

            System.out.print("Customer ID");
            System.out.print("       ");

            System.out.print("Customer Name");
            System.out.print("          ");
            int itemCounter = 1;
            while (rowsNumber.length > 0 && rowsNumber[rowsNumber.length - 1] >= itemCounter) {
                System.out.print("Item " + itemCounter);
                System.out.print("         ");
                System.out.print("Quantity");
                System.out.print("     ");
                System.out.print("Price");
                System.out.print("      ");
                itemCounter++;
            }
            System.out.print(" Order Total Price");
            System.out.println();
            System.out.println();
            while (counter < customerList.size()) {

                System.out.print("|");
                System.out.print(orderList.get(counter).getId());
                System.out.print("|");
                for (int r = 10 - String.valueOf(orderList.get(counter).getId()).length(); r > 0; r--) {
                    System.out.print(" ");

                }
                System.out.print("| ");
                System.out.print(orderList.get(counter).getDate());
                for (int r = 16 - orderList.get(counter).getDate().toString().length(); r >= 0; r--) {
                    System.out.print(" ");
                }

                System.out.print("|");

                System.out.print(customerList.get(counter).getId());
                System.out.print("|");
                for (int r = 16 - String.valueOf(customerList.get(counter).getId()).length(); r > 0; r--) {
                    System.out.print(" ");
                }

                System.out.print("| ");

                System.out.print(customerList.get(counter).getName());
                for (int r = 20 - customerList.get(counter).getName().length(); r >= 0; r--) {
                    System.out.print(" ");
                }
                System.out.print("| ");
                int rr = 0;
                while (order.get(counter + 1).next()) {

                    System.out.print(printer = order.get(counter + 1).getString("inventory.product_name"));
                    for (int i = 13 - printer.length(); i > 0; i--) {
                        System.out.print(" ");
                    }
                    System.out.print("| ");
                    System.out.print(printer = order.get(counter + 1).getInt("order_details.quantity") + "");
                    for (int i = 11 - printer.length(); i > 0; i--) {
                        System.out.print(" ");
                    }
                    System.out.print("| ");
                    System.out.print(printer = order.get(counter + 1).getFloat("order_details.price") + "");

                    for (int i = 9 - printer.length(); i > 0; i--) {
                        System.out.print(" ");
                    }
                    System.out.print("| ");

                    rr++;

                }
                while (rr < rowsNumber[rowsNumber.length - 1]) {

                    for (int i = 13; i > 0; i--) {
                        System.out.print(" ");

                    }
                    System.out.print("|");
                    for (int i = 12; i > 0; i--) {
                        System.out.print(" ");

                    }
                    System.out.print("|");
                    for (int i = 10; i > 0; i--) {
                        System.out.print(" ");

                    }
                    System.out.print("| ");

                    rr++;
                }
                System.out.print("| ");

                System.out.print(orderList.get(counter).getOrderPrice());

                System.out.println();
                counter++;
            }

        } catch (SQLException ex) {
            if(ex instanceof SQLDataException){
                System.out.println("WARNING*** This Order Does Not Exist");
            }else{
                System.out.println(ex.getMessage());
            }

        } finally {
            order.clear();

            customerList.clear();
            orderList.clear();

            try {
                if (detailsStatement != null) {
                    detailsStatement.close();

                }
                if (detailsResult != null) {
                    detailsResult.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }

            disconnect();
        }

	}
	
	@Override
	public void updateOrder(String table, String field) {
		connect();
        StopOrder:
        {
            char answer;
            int orderID;
            int currentCustomerID;
            getOrders();
            while (true) {
                System.out.println("Please Enter The ID of The Order You Want To Modify");
                while (true) {
                    try {
                        orderID = in.nextInt();
                        in.nextLine();
                        break;
                    } catch (InputMismatchException ex) {
                        in.nextLine();
                        System.out.println("Invalid Input Please Enter a Valid Value");
                    }
                }

                try {
                    statement = connect.prepareStatement("select *from orders where id = ?");
                    statement.setInt(1, orderID);
                    resultSet = statement.executeQuery();

                    if (!resultSet.next()) {
                        if (orderIsNotExist()) {
                            break StopOrder;
                        }
                    } else {
                        currentCustomerID = resultSet.getInt("customer_id");
                        break;
                    }
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
            try {
                if (table.toLowerCase().equals("orders")) {

                    ////////////////////////////////////////////////
                    ///////////////////////////////////////////////
                    if (field.equals("customer_id")) {

                        int newCustomerID;
                        while (true) {
                            System.out.println("Please Enter The ID of The Customer");
                            while (true) {
                            	
                            	if (connect.isClosed()) {
                                	connect();
                                }

                                try {
                                    newCustomerID = in.nextInt();
                                    in.nextLine();
                                    break;
                                } catch (InputMismatchException ex) {
                                    in.nextLine();
                                    System.out.println("Invalid Input Please Enter a Valid Value");
                                }
                            }

                            try {
                                statement = connect.prepareStatement("select *from customers where id = ?");
                                statement.setInt(1, newCustomerID);
                                resultSet = statement.executeQuery();
                                if (!resultSet.next()) {
                                    throw new SQLException();
                                }

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
                                        customerService.getCustomers();
                                    }
                                    if (answer == 'A') {
                                        break;
                                    }
                                    if (answer == 'E') {
                                        break StopOrder;
                                    }
                                }

                            }
                        }
                        statement = 
                        		connect.prepareStatement("update orders set customer_id = ? where id = ?");
                        statement.setInt(1, newCustomerID);
                        statement.setInt(2, orderID);
                        statement.execute();

                        System.out.println("The Customer ID for Order Which Its ID (" + orderID + ")"
                        		+ " Has Changed from (" + currentCustomerID + ") to "
                        				+ "(" + newCustomerID + ")");
                    }

                    ////////////////////////////////////////////////
                    ///////////////////////////////////////////////
                    if (field.toLowerCase().equals("date")) {

                        try {
                            statement = connect.prepareStatement("select date from orders where id = ?");
                            statement.setInt(1, orderID);
                            resultSet = statement.executeQuery();
                            resultSet.next();
                            String currentDate = resultSet.getString("date");
                            System.out.println("The Current Date for this Order is (" + currentDate + ")");

                            String newDate;
                            System.out.println("Please Enter the new Date In This Format (Year / Month / Day)");
                            newDate = in.next();
                            statement = connect.prepareStatement("update orders set date = ? where id = ?");
                            statement.setString(1, newDate);
                            statement.setInt(2, orderID);
                            statement.execute();

                            System.out.println("The Date Has Changed from (" + currentDate + ") to (" + newDate + ")");
                        } catch (SQLException ex) {
                            System.out.println(ex.getMessage());
                        }

                    }

                } else if (table.equals("order_details")) {

                    ////////////////////////////////////////////////
                    ///////////////////////////////////////////////
                    if (field.equals("quantity")) {
                        String productName;
                        int newQuantity = 0;
                        int currentQuantity = 0;
                        statement = connect.prepareStatement("Select order_details.quantity,inventory.product_name,order_details.price,"
                                + "order_details.product_id from order_details , inventory where order_id = ? "
                                + "and order_details.product_id = inventory.product_id");
                        statement.setInt(1, orderID);
                        resultSet = statement.executeQuery();

                        float updatedPrice = 0;
                        while (resultSet.next()) {
                            productName = resultSet.getString("inventory.product_name");
                            currentQuantity = resultSet.getInt("order_details.quantity");
                            System.out.println("This Order has " + productName);
                            System.out.println("Do you Want to Modify (" + productName + ") Quantity "
                                    + "Which is Currently (" + currentQuantity + ") Answer With (Y/N)");

                            answer = in.next().toUpperCase().charAt(0);

                            while (answer != 'Y' && answer != 'N') {
                                System.out.println("Invalid Answer Please Answer With (Y/N)");
                                answer = in.next().toUpperCase().charAt(0);
                            }
                            if (answer == 'Y') {
                                System.out.println("Please Enter The New Quantity");
                                while (true) {
                                    try {
                                        newQuantity = in.nextInt();
                                        if (newQuantity < 0) {
                                            throw new InputMismatchException();
                                        }
                                        break;
                                    } catch (InputMismatchException ex) {
                                        in.nextLine();
                                        System.out.println("Invalid Value Please Enter a Valid Value");
                                    }
                                }

                                statement = connect.prepareStatement("update order_details set quantity = ? where order_id = ? "
                                        + "and product_id = ?");
                                statement.setFloat(1, newQuantity);
                                statement.setInt(2, orderID);
                                statement.setInt(3, resultSet.getInt("product_id"));
                                statement.execute();

                                statement = connect.prepareStatement("update inventory set product_quantity = product_quantity + ? where product_id = ?");
                                statement.setInt(1, resultSet.getInt("quantity") - newQuantity);
                                statement.setInt(2, resultSet.getInt("order_details.product_id"));
                                statement.execute();

                                updatedPrice += newQuantity * resultSet.getFloat("order_details.price");
                            } else {
                                updatedPrice += resultSet.getInt("order_details.quantity") * resultSet.getFloat("order_details.price");
                            }

                        }

                        statement = connect.prepareStatement("update orders set Order_Total_Price = ? where id = ?");
                        statement.setFloat(1, updatedPrice);
                        statement.setInt(2, orderID);
                        statement.execute();

                        System.out.println("The Quantity for Order Which Its ID (" + orderID + ") Has Changed from (" + currentQuantity + ") to (" + newQuantity + ")");

                    }
                    ////////////////////////////////////////////////
                    ///////////////////////////////////////////////
                    /////////////////////////////////
                    if (field.equals("price")) {
                        float updatedPrice = 0;
                        String productName;
                        float currentPrice;
                        statement = connect.prepareStatement("Select order_details.quantity,inventory.product_name,order_details.price"
                                + ",order_details.product_id from order_details, inventory where order_id = ?"
                                + " and order_details.product_id = inventory.product_id");
                        statement.setInt(1, orderID);
                        resultSet = statement.executeQuery();

                        while (resultSet.next()) {
                            productName = resultSet.getString("inventory.product_name");
                            currentPrice = resultSet.getFloat("order_details.price");
                            System.out.println("This Order has " + productName);
                            System.out.println("Do you Want to Modify (" + productName + ") price Which is Currently ("
                                    + currentPrice + ") Answer With (Y/N)");

                            answer = in.next().toUpperCase().charAt(0);

                            while (answer != 'Y' && answer != 'N') {
                                System.out.println("Invalid Answer Please Answer With (Y/N)");
                                answer = in.next().toUpperCase().charAt(0);
                            }

                            if (answer == 'Y') {
                                float newPrice;
                                System.out.println("Enter the New Price");
                                newPrice = in.nextInt();
                                statement = connect.prepareStatement("update order_details set price = ? where order_id = ? "
                                        + "and product_id = ?");
                                statement.setFloat(1, newPrice);
                                statement.setInt(2, orderID);
                                statement.setInt(3, resultSet.getInt("order_details.product_id"));
                                statement.execute();

                                System.out.println("The Price of Product (" + productName + ") Has Changed from (" + currentPrice + ") to (" + newPrice + ") "
                                        + "in The Order Which Its ID (" + orderID + ")");

                                updatedPrice += resultSet.getInt("order_details.quantity") * newPrice;

                            } else {
                                updatedPrice += resultSet.getInt("order_details.quantity") * resultSet.getFloat("order_details.price");
                            }
                        }
                        statement = connect.prepareStatement("update orders set Order_Total_Price = ? where id = ?");

                        statement.setFloat(1, updatedPrice);
                        statement.setInt(2, orderID);
                        statement.execute();

                    }
                }

            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }

        }
        disconnect();

		
	}

	@Override
	public void deleteOrder() {
		connect();
        StopOrderDelete:
        {
            char answer;
            int orderID;
            while (true) {
                System.out.println("Enter The ID or The Order You Want to Delete");

                while (true) {
                    try {
                        orderID = in.nextInt();
                        in.nextLine();
                        if (orderID < 0) {
                            throw new InputMismatchException();
                        }
                        break;
                    } catch (InputMismatchException ex) {
                        in.nextLine();
                        System.out.println("Invalid Input Please Enter a Valid Value");
                    }
                }

                try {
                    statement = connect.prepareStatement("select * from orders where id = ?");
                    statement.setInt(1, orderID);
                    resultSet = statement.executeQuery();
                    if (!resultSet.next()) {
                        if (orderIsNotExist()) {
                            break StopOrderDelete;
                        }
                    } else {
                        break;
                    }
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }

            }

            System.out.println("This Is the Order You Want To Delete Do you Want To "
            		+ "Continue The Deletion Process?!");
            
            getOrderDetails(orderID);
            
            System.out.println("""
                               Press (C) to Continue the Deletion
                               Press (E) to Back To the Main Menu
                               """);
            answer = in.next().toUpperCase().charAt(0);
            while (answer != 'C' && answer != 'E') {
                System.out.println("""
                                   Invalid Answer...
                                   Press (C) to Continue the Deletion
                                   Press (E) to Back To the Main Menu
                                   """);
                answer = in.next().toUpperCase().charAt(0);
            }
            if (answer == 'E') {
                break StopOrderDelete;
            }
            int productID;
            int productQuantity;
            try {

                statement = 
                		connect.prepareStatement("select product_id, quantity from order_details where order_id = ?");
                statement.setInt(1, orderID);
                resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    productID = resultSet.getInt("product_id");
                    productQuantity = resultSet.getInt("quantity");
                    statement = 
                    		connect.prepareStatement("update inventory set product_quantity = product_quantity + ? where product_id = ?");
                    
                    statement.setInt(1, productQuantity);
                    statement.setInt(2, productID);
                    statement.execute();
                    System.out.println(productID + "                   " + productQuantity);
                }

                statement = connect.prepareStatement("delete from orders where id = ?");
                statement.setInt(1, orderID);
                statement.execute();
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());

            }

        }
        disconnect();
		
	}

	public boolean orderIsExist() {
        boolean checker = false;
        char answer;
        Stop:
        {
            
            while (true) {
                System.out.println("""
                                   Press (A) to Add Another ID 
                                   Press (S) to show the Applied Orders
                                   Press (E) to Back To The Main Menu
                                   """);
                answer = in.next().toUpperCase().charAt(0);
                if (answer != 'A' && answer != 'S' && answer != 'E') {
                    System.out.println("Invalid Input ......");
                }
                if (answer == 'A') {
                    break;
                }
                if (answer == 'S') {
                    getOrders();
                }
                if (answer == 'E') {
                    checker = true;
                    break Stop;
                }
            }
        }
        return checker;
    }

    public boolean orderIsNotExist() {
        boolean checker;
        System.out.println("This Order ID is Not Exists...");
        checker = orderIsExist();
        return checker;
    }
    
    public boolean productIsNotExist() {
        char answer;
        boolean checker = false;
        Stop:
        {
        	System.out.println("This Product Does not Exist in the Inventory");
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
