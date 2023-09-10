/**
 * This class provides the methods required to execute CRUD operations in the database in the 'stocks' table
 */

package com.booklibrary.stock;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import com.booklibrary.members.MemberDatabaseUtil;

public class StockDatabaseUtil {
	
	private static StockDatabaseUtil instance;
	private DataSource dataSource;
	private String jndiName = "java:comp/env/jdbc/jdbc/booklibrary";
	
	public static StockDatabaseUtil getInstance() throws Exception {
		if (instance == null) {
			instance = new StockDatabaseUtil();
		}
		return instance;
	}
	
	private StockDatabaseUtil() throws Exception {		
		dataSource = getDataSource();
	}
	
	private DataSource getDataSource() throws NamingException {
		Context context = new InitialContext();
		
		DataSource theDataSource = (DataSource) context.lookup(jndiName);
		
		return theDataSource;
	}
	
	/**
	 * Query all the stocks in the database
	 * @return members with 'active' status in the database
	 * @throws Exception
	 */
	public List<Stock> getAllStock() throws Exception {

		List<Stock> stockList = new ArrayList<>();

		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;

		try {
			connection = getConnection();

			String sql = "select * from stocks";

			statement = connection.createStatement();

			resultSet = statement.executeQuery(sql);

			// process result set
			while (resultSet.next()) {

				// retrieve data from result set row
				int stock_id = resultSet.getInt("stock_id");
				String author = resultSet.getString("author");
				String title = resultSet.getString("title");
				String category = resultSet.getString("category");
				int number_of_copies = resultSet.getInt("number_of_copies");

				// create new student object
				Stock tempStock = new Stock(stock_id, author, title, category, number_of_copies);

				// add it to the list of students
				stockList.add(tempStock);
			}

			return stockList;
		} finally {
			close(connection, statement, resultSet);
		}
	}
	
	/**
	 * Add a new stock to the database
	 * @param newStock a Stock object that will persist in the database
	 * @throws Exception
	 */
	public void addStock(Stock newStock) throws Exception {

		Connection connection = null;
		PreparedStatement statement = null;

		try {
			connection = getConnection();

			String sql = "insert into stocks (author, title, category, number_of_copies) values (?, ?, ?, ?)";

			statement = connection.prepareStatement(sql);

			// set params
			statement.setString(1, newStock.getAuthor());
			statement.setString(2, newStock.getTitle());
			statement.setString(3, newStock.getCategory());
			statement.setInt(4, newStock.getNumberOfCopies());

			statement.execute();
		} finally {
			close(connection, statement);
		}

	}

	/*
	 * Decrease the number of copies by 1 when an item gets borrowed
	 */
	public void decreaseNumberOfCopies(Stock stock) throws Exception {
		Connection connection = null;
		PreparedStatement statement = null;

		try {
			connection = getConnection();

			String sql = "update stocks " + " set number_of_copies=?" + " where stock_ID=?";

			statement = connection.prepareStatement(sql);

			// set params
			int quantity = stock.getNumberOfCopies() - 1;
			statement.setInt(1, quantity);
			statement.setInt(2, stock.getStockId());
			statement.execute();
		} finally {
			close(connection, statement);
		}
	}

	/*
	 * Increase the number of copies by 1 when an item gets returned
	 */
	public void increaseNumberOfStock(Stock stock) throws Exception {
		Connection connection = null;
		PreparedStatement statement = null;

		try {
			connection = getConnection();

			String sql = "update stocks " + " set number_of_copies=?" + " where stock_ID=?";

			statement = connection.prepareStatement(sql);

			// set params
			int quantity = stock.getNumberOfCopies() + 1;
			statement.setInt(1, quantity);
			statement.setInt(2, stock.getStockId());
			statement.execute();
		} finally {
			close(connection, statement);
		}
	}

	/**
	 * Query from the stocks table
	 * @param stockId the id of the row to be queried
	 * @return Stock object if found
	 * @throws Exception if not found in the database
	 */
	public Stock getStock(int stockId) throws Exception {

		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;

		try {
			connection = getConnection();

			String sql = "select * from stocks where stock_ID=?";

			statement = connection.prepareStatement(sql);

			// set params
			statement.setInt(1, stockId);

			resultSet = statement.executeQuery();

			Stock theStock = null;

			// retrieve data from result set row
			if (resultSet.next()) {
				int stock_ID = resultSet.getInt("stock_ID");
				String author = resultSet.getString("author");
				String title = resultSet.getString("title");
				String category = resultSet.getString("category");
				int number_of_copies = resultSet.getInt("number_of_copies");

				theStock = new Stock(stock_ID, author, title, category, number_of_copies);
			} else {
				throw new Exception("Could not find member id: " + stockId);
			}

			return theStock;
		} finally {
			close(connection, statement, resultSet);
		}
	}

	/**
	 * Modify an existing record in the database
	 * @param stock the id of the record to be modified
	 * @throws Exception if the id wasn't found
	 */
	public void updateStock(Stock stock) throws Exception {

		Connection connection = null;
		PreparedStatement statement = null;

		try {
			connection = getConnection();

			String sql = "update stocks " + " set author=?, title=?, category=?, number_of_copies=?" + " where stock_ID=?";

			statement = connection.prepareStatement(sql);

			// set params

			statement.setString(1, stock.getAuthor());
			statement.setString(2, stock.getTitle());
			statement.setString(3, stock.getCategory());
			statement.setInt(4, stock.getNumberOfCopies());
			statement.setInt(5, stock.getStockId());
			statement.execute();
		} finally {
			close(connection, statement);
		}

	}

	/**
	 * Delete a stock item from the database
	 * @param stockId the id of the stock to be deleted
	 * @throws Exception if the id couldn't be found
	 */
	public void deleteStock(int stockId) throws Exception {

		Connection connection = null;
		PreparedStatement statement = null;

		try {
			connection = getConnection();

			String sql = "delete from stocks where stock_ID=?";

			statement = connection.prepareStatement(sql);

			// set params
			statement.setInt(1, stockId);

			statement.execute();
		} finally {
			close(connection, statement);
		}
	}

	private Connection getConnection() throws Exception {

		Connection connection = dataSource.getConnection();

		return connection;
	}

	private void close(Connection connection, Statement statement) {
		close(connection, statement, null);
	}

	private void close(Connection connection, Statement statement, ResultSet resultSet) {

		try {
			if (resultSet != null) {
				resultSet.close();
			}

			if (statement != null) {
				statement.close();
			}

			if (connection != null) {
				connection.close();
			}

		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

}
