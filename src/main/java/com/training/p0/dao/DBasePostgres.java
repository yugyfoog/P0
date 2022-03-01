package com.training.p0.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedList;
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
		final String sql = "SELECT "
						 + "    account_number, "
				         + "    first_name, "
						 + "    last_name, "
				         + "    balance, "
						 + "    approved "
				         + "FROM "
						 + "    p0_users "
				         + "NATURAL JOIN "
						 + "    p0_accounts "
				         + "WHERE "
						 + "    userid=?";
		try {
			stmt = connection.prepareStatement(sql);
			stmt.setInt(1, userId);
			ResultSet results = stmt.executeQuery();
			while (results.next()) {
				AccountInfo entry = new AccountInfo(
						results.getInt("account_number"),
						userId,
						results.getString("first_name"),
						results.getString("last_name"),
						results.getInt("balance"),
						results.getBoolean("approved"));
				entries.add(entry);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return entries;
	}

	@Override
	public List<AccountInfo> getAccountsPendingApproval() {
		final String sql = "SELECT "
				         + "    account_number, "
				         + "    userid, "
				         + "    first_name, "
				         + "    last_name, "
				         + "    balance, "
				         + "    approved "
				         + "FROM "
				         + "    p0_users "
				         + "NATURAL JOIN "
				         + "    p0_accounts "
				         + "WHERE "
				         + "    approved=false";
		List<AccountInfo> accounts = new LinkedList<AccountInfo>();
		try {
			Statement stmt = connection.createStatement();
			ResultSet results = stmt.executeQuery(sql);
			while (results.next()) {
				AccountInfo account = new AccountInfo(
						results.getInt("account_number"),
						results.getInt("userid"),
						results.getString("first_name"),
						results.getString("last_name"),
						results.getInt("balance"),
						false);
				accounts.add(account);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return accounts;
	}


	@Override
	public int approveAccounts(List<AccountInfo> accounts) {
		final String sql = "UPDATE p0_accounts SET approved=TRUE WHERE account_number=?";
		int rows = 0;
		for (AccountInfo account : accounts) {
			try {
				PreparedStatement stmt = connection.prepareStatement(sql);
				stmt.setInt(1, account.getAccountNumber());
				rows += stmt.executeUpdate();
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}
		return rows;
	}

	@Override
	public int denyAccounts(List<AccountInfo> accounts) {
		final String sql = "DELETE FROM p0_accounts WHERE account_number=?";
		int rows = 0;
		for (AccountInfo account : accounts) {
			try {
				PreparedStatement stmt = connection.prepareStatement(sql);
				stmt.setInt(1,  account.getAccountNumber());
				rows += stmt.executeUpdate();
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}
		return rows;
	}

	@Override
	public int accountUpdate(int account, int amount) {
		// get account information
		
		final String sql1 = "SELECT balance, approved FROM p0_accounts WHERE account_number=?";
		ResultSet results = null;
		int queryBalance;
		boolean queryApproved;
		try {
			PreparedStatement stmt = connection.prepareStatement(sql1);
			stmt.setInt(1, account);
			results = stmt.executeQuery();
			if (results.next()) {
				queryBalance = results.getInt("balance");
				queryApproved = results.getBoolean("approved");
			}
			else
				return -2; // account not found;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return -3;  // unknown error
		}
		
		if (queryApproved == false)
			return -5; // account not approved yet
		
		int newBalance = queryBalance + amount;
		if (newBalance < 0)
			return -1; // negative balance!
		
		// update account
		
		final String sql2 = "UPDATE p0_accounts SET balance=? WHERE account_number=?";
		int rows = 0;
		try {
			PreparedStatement stmt = connection.prepareStatement(sql2);
			stmt.setInt(1, newBalance);
			stmt.setInt(2, account);			
			rows = stmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		if (rows == 0) 
			return -6; // account not updated
		
		if (amount >= 0)
			logTransaction("Deposit of " + amount + " into " + account);
		else
			logTransaction("Withdraw of " + -amount + " from " + account);
		
		return newBalance;
	}

	@Override
	public int transfer(int sender, int receiver, int amount) {
		int senderBalance = -1;
		// int receiverBalance = -1;
		
		try {
			CallableStatement stat = connection.prepareCall("call money_transfer(?,?,?,?,?)");
			stat.setInt(1, sender);
			stat.setInt(2, receiver);
			stat.setInt(3, amount);
			stat.registerOutParameter(4, Types.INTEGER);
			stat.setInt(4, -1);
			stat.registerOutParameter(5, Types.INTEGER);
			stat.setInt(5,  -1);
			stat.execute();
		
			senderBalance = stat.getInt(4);
			// receiverBalance = stat.getInt(5);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		logTransaction("Transfer of " + amount + " from " + sender + " to " + receiver);
		return senderBalance;
	}

	private void logTransaction(String msg) {
		final String sql = "INSERT INTO p0_log VALUES ( now(), ?)";
		try {
			PreparedStatement stmt = connection.prepareStatement(sql);
			stmt.setString(1,  msg);
			stmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	@Override
	public List<String> viewTransactions() {
		List<String> transactions = new LinkedList<String>();
		final String sql = "SELECT * FROM p0_log";
		ResultSet results = null;
		try {
			Statement stmt = connection.createStatement();
			results = stmt.executeQuery(sql);
			while (results.next()) {
				Date date = results.getDate("time_date");
				String entry = results.getString("entry");
				transactions.add(date + " " + entry);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return transactions;
	}
	
}
