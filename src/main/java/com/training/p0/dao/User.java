package com.training.p0.dao;

public class User {		
	
	int userId;
	private String firstName;
	private String lastName;
	private boolean isEmployee;
	
	public User(int userId, String firstName, String lastName, boolean isEmployee) {
		this.userId = userId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.isEmployee = isEmployee;
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

	public boolean getIsEmployee() {
		return isEmployee;
	}

	public void setEmployee(boolean isEmployee) {
		this.isEmployee = isEmployee;
	}
	
	
}
