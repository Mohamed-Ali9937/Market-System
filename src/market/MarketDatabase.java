package market;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MarketDatabase {

	private String dbUrl = "jdbc:mysql://localhost/Market";
	private String username = "root";
	private String password = "Javamysql";
	private Connection connect;
	private static MarketDatabase database;

	private MarketDatabase() {

	}

	public static MarketDatabase getDatabase() {
		if (database == null) {
			synchronized (MarketDatabase.class) {
				if (database == null) {
					database = new MarketDatabase();
				}
			}
		}
		return database;
	}

	public Connection connect() {
		try {
			connect = DriverManager.getConnection(dbUrl, username, password);
		} catch (SQLException ex) {
			System.out.println(ex.getMessage());
		}
		return connect;
	}

	public void disconnect(PreparedStatement statement, ResultSet resultSet, Connection connect) {
		try {
			if (statement != null) {
				statement.close();
			}
			
			if (resultSet != null) {
				resultSet.close();
			}
			
			if (!connect.isClosed()) {
				connect.close();
			}

		} catch (SQLException ex) {
			System.out.println(ex.getMessage());
		}
	}
}
