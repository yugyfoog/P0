package com.training.p0.dao;

import java.util.List;

public interface DBaseDAO {
	// error codes
	public static final int ERROR_NEGATIVE_BALANCE = -1;
	public static final int ERROR_ACCOUNT_NOT_FOUND = -2;
	public static final int ERROR_SQL_ERROR = -3;
	public static final int ERROR_ACCOUNT_NOT_APPROVED = -5;
	public static final int ERROR_ACCOUNT_NOT_UPDATED = -6;
	User getUser(int userId);
	boolean addUser(User user);
	boolean newAccount(int userId, int amount);
	List<AccountInfo> viewAccount(int userId);
	List<AccountInfo> getAccountsPendingApproval();
	int approveAccounts(List<AccountInfo> accounts);
	int denyAccounts(List<AccountInfo> accounts);
	int accountUpdate(int account, int amount);
	int transfer(int sender, int receiver, int amount);
	int getUserId(int accountNumber);
	List<String> viewTransactions();
}
