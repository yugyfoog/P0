package com.training.p0.dao;

import java.util.List;

public class Credentials {

	private String username;
	private String password;
	
	public Credentials(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	public Credentials(List<String> credentials) {
		username = credentials.get(0);
		password = credentials.get(1);
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	
}
