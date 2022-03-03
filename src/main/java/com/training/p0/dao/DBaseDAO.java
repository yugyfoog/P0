package com.training.p0.dao;

import java.util.List;

public interface DBaseDAO {
	
	public static final int ERROR_NEGATIVE_BALANCE = -1;
	public static final int ERROR_ACCOUNT_NOT_FOUND = -2;
	public static final int ERROR_SQL_ERROR = -3;
	public static final int ERROR_ACCOUNT_NOT_APPROVED = -5;
	public static final int ERROR_ACCOUNT_NOT_UPDATED = -6;
	
	public User getUser(int userId);
	public boolean addUser(User user);
	public boolean newAccount(int userId, int amount);
	public List<AccountInfo> viewAccounts(int userId);
	public AccountInfo getAccountInfo(int accountNumber);
	public List<AccountInfo> getAccountsPendingApproval();
	public int approveAccounts(List<AccountInfo> accounts);
	public int denyAccounts(List<AccountInfo> accounts);
	public int accountUpdate(int account, int amount);
	public int transfer(int sender, int receiver, int amount);
	public int getUserId(int accountNumber);
	public List<String> viewTransactions();
}
