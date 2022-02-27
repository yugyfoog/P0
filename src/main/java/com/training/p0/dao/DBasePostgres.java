package com.training.p0.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.training.p0.consoleapp.AccountInfo;
import com.training.p0.consoleapp.User;

public class DBasePostgres implements DBaseDAO {
	Connection connection = PostgresConnection.getConnection();
	

	@Override
	public User getUser(int userId) {
		PreparedStatement stmt;
		User user = null;
		final String sql = "SELECT * FROM p0_users WHERE userid=?";
		try {
			stmt = connection.prepareStatement(sql);
			stmt.setInt(1, userId);
			ResultSet results = stmt.executeQuery();
			if (results.next()) 
				user = new User(
						results.getInt("userid"),
						results.getString("first_name"),
						results.getString("last_name"),
						results.getBoolean("is_employee"));
		} catch (SQLException e) {
			System.out.println("user id not found: " + userId);
		}
		return user;
	}


	@Override
	public boolean addUser(User user) {
		PreparedStatement stmt;
		
		final String sql = "INSERT INTO p0_users (userid, first_name, last_name, is_employee) VALUES (?, ?, ?, ?)";
		int rows = 0;
		try {
			stmt = connection.prepareStatement(sql);
			stmt.setInt(1,  user.getUserId());
			stmt.setString(2,  user.getFirstName());
			stmt.setString(3,  user.getLastName());
			stmt.setBoolean(4,  user.getIsEmployee());
			rows = stmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return rows == 1;
	}

	@Override
	public boolean newAccount(int userId, int amount) {
		PreparedStatement stmt;
		
		final String sql = "INSERT INTO p0_accounts (userid, balance, approved) VALUES (?, ?, ?)";
		int rows = 0;
		try {
			stmt = connection.prepareStatement(sql);
			stmt.setInt(1, userId);
			stmt.setInt(2, amount);
			stmt.setBoolean(3, false); // accounts need to be approved
			rows = stmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return rows == 1;
	}

	@Override
	public List<AccountInfo> viewAccount(int userId) {
		PreparedStatement stmt;
		List<AccountInfo> entries = new ArrayList<AccountInfo>();
		final String sql = "SELECT * FROM p0_accounts WHERE userid=?";
		try {
			stmt = connection.prepareStatement(sql);
			stmt.setInt(1, userId);
			ResultSet results = stmt.executeQuery();
			while (results.next()) {
				AccountInfo entry = new AccountInfo(
						results.getInt("account_number"),
						userId,
						results.getInt("balance"),
						results.getBoolean("approved"));
				entries.add(entry);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return entries;
	}
	
}
