package com.recommender;

import org.apache.commons.lang.ArrayUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JDBCExample {

	Connection conn = null;
	Statement stmt = null;

	public static void main1(String[] args) throws SQLException {
		JDBCExample jdbc = new JDBCExample();
		System.out.println(jdbc.friendsList(1));
	}

	public JDBCExample() {
		System.out.println("-------- PostgreSQL " + "JDBC Connection Testing ------------");

		try {

			Class.forName("org.postgresql.Driver");

		} catch (ClassNotFoundException e) {

			System.out.println("Where is your PostgreSQL JDBC Driver? " + "Include in your library path!");
			e.printStackTrace();
			return;

		}

		System.out.println("PostgreSQL JDBC Driver Registered!");

		Connection connection = null;

		try {

			connection = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/dataset_foursquare_processed",
					"postgres", "admin");

		} catch (SQLException e) {

			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return;

		}

		if (connection != null) {
			System.out.println("You made it, take control your database now!");
		} else {
			System.out.println("Failed to make connection!");
		}
		conn = connection;
	}

	public List<Long> friendsList(long userID) throws SQLException {
		stmt = conn.createStatement();
		String sql;
		sql = "SELECT distinct id_friend FROM relations where id_user=" + userID + " ";
		//System.out.println(sql);
		ResultSet rs = stmt.executeQuery(sql);
		List<Long> result = new ArrayList<Long>();
		// STEP 5: Extract data from result set
		while (rs.next()) {
			// Retrieve by column name
			long id = rs.getInt("id_friend");
			result.add(new Long(id));
		}
		// STEP 6: Clean-up environment
		rs.close();
		return result;
	}

	public void closeConnection() throws Exception {
		stmt.close();
	}

	public long[] friendsFriendsList(long userID) throws SQLException {
		long[] neighborhood = null;
		List<Long> result = new ArrayList<Long>();
		List<Long> friends = this.friendsList(userID);
		result.addAll(friends);
		int limit=32500;
		if (result.size() < limit) {
			for (Long friend : friends) {
				if (result.size() < limit) {
					List<Long> friendsFriends = this.friendsList(friend);
					result.addAll(friendsFriends);
				}
			}
		}

		long[] intArray = ArrayUtils.toPrimitive(result.toArray(new Long[result.size()]));
		return intArray;
	}


}