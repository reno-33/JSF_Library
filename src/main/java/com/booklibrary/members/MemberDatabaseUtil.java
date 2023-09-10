/**
 * This class provides the methods required to execute CRUD operations in the database in the 'members' table
 */

package com.booklibrary.members;

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

public class MemberDatabaseUtil {
	
	private static MemberDatabaseUtil instance;
	private DataSource dataSource;
	private String jndiName = "java:comp/env/jdbc/jdbc/booklibrary";
	
	public static MemberDatabaseUtil getInstance() throws Exception {
		if (instance == null) {
			instance = new MemberDatabaseUtil();
		}
		return instance;
	}
	
	private MemberDatabaseUtil() throws Exception {		
		dataSource = getDataSource();
	}
	
	private DataSource getDataSource() throws NamingException {
		Context context = new InitialContext();
		
		DataSource theDataSource = (DataSource) context.lookup(jndiName);
		
		return theDataSource;
	}
	
	/**
	 * Query all the active members in the database
	 * @return members with 'active' status in the database
	 * @throws Exception
	 */
	public List<Member> getMembers() throws Exception {

		List<Member> membersList = new ArrayList<>();

		Connection connection = null;
		Statement statement = null;
		ResultSet myRs = null;
		
		try {
			connection = getConnection();

			String sql = "select * from members where status = 'active'";

			statement = connection.prepareStatement(sql);


			myRs = statement.executeQuery(sql);

			// process result set
			while (myRs.next()) {
				
				// retrieve data from result set row
				int member_ID = myRs.getInt("member_ID");
				String name = myRs.getString("name");
				String address = myRs.getString("address");
				String phone_number = myRs.getString("phone_number");
				String id_card_number = myRs.getString("id_card_number");
				String status = myRs.getString("status");

				// create new member object
				Member tempMember = new Member(member_ID, name, address, phone_number, id_card_number, status);

				// add it to the list of members
				membersList.add(tempMember);
			}
			
			return membersList;		
		}
		finally {
			close (connection, statement, myRs);
		}
	}
	
	/**
	 * Add a new member to the database
	 * @param newMember a Member object that will persist in the database
	 * @throws Exception
	 */
	public void addMember(Member newMember) throws Exception {

		Connection connection = null;
		PreparedStatement statement = null;

		try {
			connection = getConnection();

			String sql = "insert into members (name, address, phone_number, id_card_number) values (?, ?, ?, ?)";

			statement = connection.prepareStatement(sql);

			// set parameters
			statement.setString(1, newMember.getName());
			statement.setString(2, newMember.getAddress());
			statement.setString(3, newMember.getPhoneNumber());
			statement.setString(4, newMember.getIdCardNumber());
			
			statement.execute();			
		}
		finally {
			close (connection, statement);
		}
	}
	
	/**
	 * Query a member from the database
	 * @param memberId the id of the member we want to query
	 * @return the Member object if found
	 * @throws Exception if the entered memberId doesn't exist the database
	 */
	public Member getMember(int memberId) throws Exception {
		
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		
		try {
			connection = getConnection();

			String sql = "select * from members where member_ID=?";

			statement = connection.prepareStatement(sql);
			
			// set params
			statement.setInt(1, memberId);
			
			resultSet = statement.executeQuery();

			Member tempMember = null;
			
			// retrieve data from result set row
			if (resultSet.next()) {
				int member_ID = resultSet.getInt("member_ID");
				String name = resultSet.getString("name");
				String address = resultSet.getString("address");
				String phone_number = resultSet.getString("phone_number");
				String id_card_number = resultSet.getString("id_card_number");
				String status = resultSet.getString("status");
				tempMember = new Member(member_ID, name, address, phone_number, id_card_number, status);
			}
			else {
				throw new Exception("Could not find member id: " + memberId);
			}

			return tempMember;
		}
		finally {
			close (connection, statement, resultSet);
		}
	}
	
	/**
	 * Modifying data for a Member object
	 * @param member the Member to be modified
	 * @throws Exception 
	 */
	public void updateMember(Member member) throws Exception {
		

		Connection connection = null;
		PreparedStatement statement = null;

		try {
			connection = getConnection();

			String sql = "update member "
						+ " set name=?, address=?, phone_number=?, id_card_number=?"
						+ " where member_ID=?";

			statement = connection.prepareStatement(sql);

			// set parameters

			statement.setString(1, member.getName());
			statement.setString(2, member.getAddress());
			statement.setString(3, member.getPhoneNumber());
			statement.setString(4, member.getIdCardNumber());
			statement.setInt(5, member.getMemberID());
			statement.execute();
		}
		finally {
			close (connection, statement);
		}
		
	}
	
	/**
	 * Delete the member from the database
	 * @param memberId of the member to be deleted
	 * @throws Exception 
	 */
	public void deleteMember(int memberId) throws Exception {

		Connection connection = null;
		PreparedStatement statement = null;

		try {
			connection = getConnection();

			String sql = "update member "
					+ " set status=?"
					+ " where member_ID=?";

			statement = connection.prepareStatement(sql);

			// set parameters
			statement.setString(1, "passive");
			statement.setInt(2, memberId);
			
			statement.execute();
		}
		finally {
			close (connection, statement);
		}		
	}	
	
	/**
	 * Get a connection to the database
	 * @return the existing connection
	 * @throws Exception
	 */
	private Connection getConnection() throws Exception {

		Connection connection = dataSource.getConnection();
		
		return connection;
	}
	
	/**
	 * Close connections and statements.
	 */
	private void close(Connection connection, Statement statement) {
		close(connection, statement, null);
	}
	
	/**
	 * Close connections, statements and resultsets
	 */
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
