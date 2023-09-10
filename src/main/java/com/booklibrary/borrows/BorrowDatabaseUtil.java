/**
 * This class provides the methods required to execute CRUD operations in the database in the 'borrows' table
 */

package com.booklibrary.borrows;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.booklibrary.stock.Stock;
import com.booklibrary.stock.StockDatabaseUtil;

public class BorrowDatabaseUtil {
	

	private static BorrowDatabaseUtil instance;
	private DataSource dataSource;
	private String jndiName = "java:comp/env/jdbc/booklibrary";

	public static BorrowDatabaseUtil getInstance() throws Exception {
		if (instance == null)
			instance = new BorrowDatabaseUtil();
		return instance;
	}

	private BorrowDatabaseUtil() throws Exception {
		dataSource = getDataSource();
	}

	private DataSource getDataSource() throws NamingException {
		Context context = new InitialContext();

		DataSource theDataSource = (DataSource) context.lookup(jndiName);

		return theDataSource;
	}
	
	/**
	 * Query all the rows from the borrows table
	 * 
	 * @return ArrayList with all the records from the database's borrows table
	 * @throws Exception
	 */
	public List<Borrow> getBorrows() throws Exception {
		List<Borrow> borrowList = new ArrayList<>();

		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;

		try {
			connection = getConnection();

			String sql = "select * from borrows";

			statement = connection.createStatement();

			resultSet = statement.executeQuery(sql);

			while (resultSet.next()) {
				int borrow_id = resultSet.getInt("borrow_id");
				int member_id = resultSet.getInt("member_id");
				int stock_id = resultSet.getInt("stock_id");
				Date date_of_borrow = resultSet.getDate("date_of_borrow");
				Date due_date = resultSet.getDate("due_date");
				Date date_of_return = resultSet.getDate("date_of_return");

				Borrow tempBorrow = new Borrow(borrow_id, member_id, stock_id, date_of_borrow, due_date,
						date_of_return);

				borrowList.add(tempBorrow);
			}

			return borrowList;
		} finally {
			close(connection, statement, resultSet);
		}
	}
	

	/**
	 * Query one borrow
	 * 
	 * @param borrowId the id of the record to be queried
	 * @return the record if the id was found in the database
	 * @throws Exception if the id was not in the database
	 */
	public Borrow getBorrow(int borrowId) throws Exception {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;

		try {
			connection = getConnection();

			String sql = "select * from borrows where borrow_id = ?";

			statement = connection.prepareStatement(sql);

			statement.setInt(1, borrowId);

			resultSet = statement.executeQuery();

			Borrow theBorrow = null;

			if (resultSet.next()) {
				int borrow_id = resultSet.getInt("borrow_id");
				int member_id = resultSet.getInt("member_id");
				int stock_id = resultSet.getInt("stock_id");
				Date date_of_borrow = resultSet.getDate("date_of_borrow");
				Date due_date = resultSet.getDate("due_date");
				Date date_of_return = resultSet.getDate("date_of_return");
				theBorrow = new Borrow(borrow_id, member_id, stock_id, date_of_borrow, due_date,
						date_of_return);
			} else {
				throw new Exception("Couldn't find borrowId: " + borrowId);
			}

			return theBorrow;
		} finally {
			close(connection, statement, resultSet);
		}
	}
	
	

	/**
	 * Method to count how many active borrows does a member have.
	 * A member's max amount of borrows can be limited with this.
	 * @param memberId the id of the member to be queried
	 * @return the number of active borrows for a member
	 * @throws Exception if the id wasn't found in the database
	 */
	public int getCount(int memberId) throws Exception {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;

		try {
			connection = getConnection();
			String sql = "select count(member_id) FROM borrows WHERE member_id=?";
			statement = connection.prepareStatement(sql);
			statement.setInt(1, memberId);
			resultSet = statement.executeQuery();

			int count = Integer.MAX_VALUE;
			if (resultSet.next()) {
				count = resultSet.getInt(1);
			} else {
				throw new Exception("The ID you entered isn't in the database");
			}

			return count;
		} finally {
			close(connection, statement, resultSet);
		}
	}
	

	/**
	 * Recording a new borrow. 
	 * The number of copies have to be decreased by 1.
	 * Date of borrow is recorded by the system, due date is the day of borrow + 30 days.
	 * @param borrow the Borrow object to be created in the database
	 * @throws Exception
	 */
	public void addBorrow(Borrow borrow) throws Exception {
		Connection connection = null;
		PreparedStatement statement = null;
		StockDatabaseUtil stockDatabaseUtil = StockDatabaseUtil.getInstance();
		Stock stock = stockDatabaseUtil.getStock(borrow.getStockId());

		try {
			connection = getConnection();

			String sql = "insert into borrows (member_id, stock_id, date_of_borrow, due_date) values (?,?,?,?)";

			// converting the date of borrow to be compatible with MySQL
			java.util.Calendar calendar = java.util.Calendar.getInstance();
			java.util.Date today = calendar.getTime();
			java.sql.Date sqlToday = new java.sql.Date(today.getTime());

			// calculating due date by adding a month to today's date
			calendar.add(java.util.Calendar.MONTH, 1);
			java.util.Date dueDate = calendar.getTime();
			java.sql.Date sqlDueDate = new java.sql.Date(dueDate.getTime());

			statement = connection.prepareStatement(sql);

			statement.setInt(1, borrow.getMemberId());
			statement.setInt(2, borrow.getStockId());
			statement.setDate(3, sqlToday);
			statement.setDate(4, sqlDueDate);

			statement.execute();

			// decrase number of copies by 1
			stockDatabaseUtil.decreaseNumberOfCopies(stock);
		} finally {
			close(connection, statement);
		}

	}


	/**
	 * Registering a returned item.
	 * The date of return is recorded by the system, no user input required.
	 * The number of copies are automatically increased.
	 * @param borrowId
	 * @throws Exception
	 */
	public void returnItem(int borrowId) throws Exception {
		Connection connection = null;
		PreparedStatement statement = null;
		Borrow borrow = getBorrow(borrowId);
		StockDatabaseUtil stockDatabaseUtil = StockDatabaseUtil.getInstance();
		Stock stock = stockDatabaseUtil.getStock(borrow.getStockId());

		try {
			connection = getConnection();

			String sql = "update borrows " + "set date_of_return = ? " + "where borrow_id = ?";

			statement = connection.prepareStatement(sql);

			// converting date format to be compatible with MySQL
			java.util.Calendar calendar = java.util.Calendar.getInstance();
			java.util.Date today = calendar.getTime();
			java.sql.Date sqlToday = new java.sql.Date(today.getTime());

			statement.setDate(1, sqlToday);
			statement.setInt(2, borrowId);

			statement.execute();

			// darabszam novelese
			stockDatabaseUtil.increaseNumberOfStock(stock);

		} finally {
			close(connection, statement);
		}

	}
	
	/**
	 * Method to check if the given user is active
	 * @param memberId the ID of the member in the database we want to examine
	 * @return true if the member is active, false if the member is inactive
	 * @throws Exception
	 */
	public boolean isActive(int memberId) throws Exception {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;

		try {
			connection = getConnection();

			String sql = "select status FROM library.members WHERE member_ID = ?";

			statement = connection.prepareStatement(sql);

			statement.setInt(1, memberId);

			resultSet = statement.executeQuery();

			String status;
			if (resultSet.next()) {
				status = resultSet.getString(1);
			} else {
				throw new Exception("The entered member doesn't exist or is inactive." );
			}
			
			if(status.equals("active"))
				return true;
			else
				return false;
		} finally {
			close(connection, statement, resultSet);
		}
	}
	
	/**
	 * Record when a borrowed item is returned. 
	 * The date of return is automatically recorded, 
	 * and the quantity of the item is increased. 
	 * @param borrowId the ID of the returned borrow
	 * @throws Exception
	 */
	public void itemReturned(int borrowId) throws Exception {
		Connection connection = null;
		PreparedStatement statement = null;
		Borrow borrow = getBorrow(borrowId);
		StockDatabaseUtil stockDatabaseUtil = StockDatabaseUtil.getInstance();
		Stock stock = stockDatabaseUtil.getStock(borrow.getStockId());

		try {
			connection = getConnection();

			String sql = "update borrows " + "set date_of_return = ? " + "where borrow_ID = ?";

			statement = connection.prepareStatement(sql);

			// Format date for MySQL
			java.util.Calendar calendar = java.util.Calendar.getInstance();
			java.util.Date today = calendar.getTime();
			java.sql.Date sqlToday = new java.sql.Date(today.getTime());

			statement.setDate(1, sqlToday);
			statement.setInt(2, borrowId);

			statement.execute();

			// increase number of copies
			stockDatabaseUtil.increaseNumberOfStock(stock);
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
