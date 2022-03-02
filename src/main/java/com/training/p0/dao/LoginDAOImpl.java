package com.training.p0.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginDAOImpl implements LoginDAO {
	Connection connection = PostgresConnection.getConnection();

	@Override
	public int newUser(Credentials credentials) {
		PreparedStatement stmt;
		
		final String sql1 = "INSERT INTO p0_passwd (username, password) VALUES (?, ?)";
		try {
			stmt = connection.prepareStatement(sql1);
			stmt.setString(1,  credentials.getUsername());
			stmt.setString(2,  credentials.getPassword());
			stmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return validate(credentials); // use validate to get userid generated userid
	}
	
	@Override
	public int validate(Credentials credentials) {
		PreparedStatement stmt;
		
		final String sql = "SELECT userid FROM p0_passwd WHERE username=? and password=?";
		int user = -1;
		try {
			stmt = connection.prepareStatement(sql);
			stmt.setString(1, credentials.getUsername());
			stmt.setString(2, credentials.getPassword());
			ResultSet result = stmt.executeQuery();
			if (result.next())
				user = result.getInt("userid");
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		return user;
	}

	@Override
	public boolean deleteUser(int userId) {
		PreparedStatement stmt;
		final String sql = "DELETE FROM p0_passwd WHERE userid=?";
		int r = -1;
		try {
			stmt = connection.prepareStatement(sql);
			stmt.setInt(1, userId);
			r = stmt.executeUpdate();
		} catch(SQLException e) {
			System.out.println(e.getMessage());
		}
		return r != -1;
	}

}
