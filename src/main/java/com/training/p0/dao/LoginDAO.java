package com.training.p0.dao;

public interface LoginDAO {
	public int newUser(Credentials credentials);
	public int validate(Credentials credentials);
	public boolean deleteUser(int userId);
	
}
