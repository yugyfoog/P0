package com.training.p0.dao;

public class AccountInfo {
	private int accountNumber;
	private int userId;
	private String firstName;
	private String lastName;
	private int balance;
	private boolean approved;
	public AccountInfo(int accountNumber, int userId, String firstName, String lastName, int balance,
			boolean approved) {
		super();
		this.accountNumber = accountNumber;
		this.userId = userId;
		this.firstName = firstName;
		this.lastName = lastName;
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
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public String getFullName() {
		return firstName + " " + lastName;
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
