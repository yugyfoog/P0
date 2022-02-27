package com.training.p0.consoleapp;

public class AccountInfo {
	private int accountNumber;
	private int userId;
	private int balance;
	private boolean approved;
	
	public AccountInfo(int accountNumber, int userId, int balance, boolean approved) {
		super();
		this.accountNumber = accountNumber;
		this.userId = userId;
		this.balance = balance;
		this.approved = approved;
	}

	public int getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(int accountNumber) {
		this.accountNumber = accountNumber;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getBalance() {
		return balance;
	}

	public void setBalance(int balance) {
		this.balance = balance;
	}

	public boolean isApproved() {
		return approved;
	}

	public void setApproved(boolean approved) {
		this.approved = approved;
	}

	
}
